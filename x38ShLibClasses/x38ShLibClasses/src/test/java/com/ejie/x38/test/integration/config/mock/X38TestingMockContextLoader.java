package com.ejie.x38.test.integration.config.mock;

public class X38TestingMockContextLoader extends X38TestingMockWebContextLoader {

	public X38TestingMockContextLoader() {
		super("test-integration/x38TestingMockWar", false);
	}

}