package com.ejie.x38.test.integration.config;

public class X38TestingContextLoader extends X38TestingWebContextLoader {

	public X38TestingContextLoader() {
		super("test-integration/x38TestingWar", false);
	}

}