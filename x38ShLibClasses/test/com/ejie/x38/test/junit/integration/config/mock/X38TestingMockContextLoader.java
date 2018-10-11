package com.ejie.x38.test.junit.integration.config.mock;

public class X38TestingMockContextLoader extends X38TestingMockWebContextLoader {

	public X38TestingMockContextLoader() {
		super("test/x38TestingMockWar", false);
	}

}