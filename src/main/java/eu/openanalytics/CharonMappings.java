/**
 * ShinyProxy
 *
 * Copyright (C) 2016-2017 Open Analytics
 *
 * ===========================================================================
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Apache License as published by
 * The Apache Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Apache License for more details.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/>
 */
package eu.openanalytics;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.github.mkopylec.charon.configuration.CharonProperties;
import com.github.mkopylec.charon.configuration.MappingProperties;
import com.github.mkopylec.charon.core.http.HttpClientProvider;
import com.github.mkopylec.charon.core.mappings.MappingsCorrector;
import com.github.mkopylec.charon.core.mappings.MappingsProvider;

import eu.openanalytics.services.DockerService;
import eu.openanalytics.services.DockerService.MappingListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties({CharonProperties.class, ServerProperties.class})
public class CharonMappings extends MappingsProvider {

	@Inject
	DockerService dockerService;

	private Map<String, URI> internalMappings = new ConcurrentHashMap<>();
	private boolean updated = false;

	@Autowired
	public CharonMappings(ServerProperties server, CharonProperties charon, MappingsCorrector mappingsCorrector, HttpClientProvider httpClientProvider) {
		super(server, charon, mappingsCorrector, httpClientProvider);
	}

	@PostConstruct
	void init() {
		dockerService.addMappingListener(new MappingListener() {
			@Override
			public void mappingAdded(String mapping, URI target) {
				internalMappings.put(mapping, target);
				updated = true;
			}

			@Override
			public void mappingRemoved(String mapping) {
				internalMappings.remove(mapping);
				updated = true;
			}
		});
	}

	@Override
	protected boolean shouldUpdateMappings(HttpServletRequest request) {
		return updated;
	}

	@Override
	protected List<MappingProperties> retrieveMappings() {
		updated = false;
		List<MappingProperties> mappings = new ArrayList<>();
		for (Map.Entry<String, URI> entry : internalMappings.entrySet()) {
			String source = entry.getKey();
			URI target = entry.getValue();
			MappingProperties mapping = new MappingProperties();
			mapping.setName(source);
			mapping.setPath(source);
			mapping.setDestinations(Arrays.asList(target.toString()));
			mappings.add(mapping);
		}
		return mappings;
	}
}