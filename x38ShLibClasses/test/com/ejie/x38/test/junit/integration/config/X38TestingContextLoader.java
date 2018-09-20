package com.ejie.x38.test.junit.integration.config;

public class X38TestingContextLoader extends X38TestingWebContextLoader {

	public X38TestingContextLoader() {
		super("test/x38TestingWar", false);
	}

}