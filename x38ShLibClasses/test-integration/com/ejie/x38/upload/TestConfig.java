package com.ejie.x38.upload;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration

//@Import(StandaloneDataConfig.class)
@ImportResource({"classpath:test-config.xml" })
public class TestConfig {

}
