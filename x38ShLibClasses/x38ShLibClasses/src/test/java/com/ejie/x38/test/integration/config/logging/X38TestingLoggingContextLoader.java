package com.ejie.x38.test.integration.config.logging;

public class X38TestingLoggingContextLoader extends X38TestingLoggingWebContextLoader {

	public X38TestingLoggingContextLoader() {
		super("test-integration/x38TestingLoggingWar", false);
	}

}