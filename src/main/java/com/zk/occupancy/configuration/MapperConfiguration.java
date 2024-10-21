package com.zk.occupancy.configuration;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfiguration {

	@Bean
	public ObjectMapper getObjectMapper() {
		var mapper = new ObjectMapper();
		mapper.configure(Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
		return mapper;
	}
}
