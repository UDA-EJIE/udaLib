package com.ejie.x38.test.junit.integration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration

//@Import(StandaloneDataConfig.class)
@ImportResource({"classpath:test-config.xml" })
public class TestConfig {

}
