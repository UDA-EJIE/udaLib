package com.ejie.x38.test.integration.config.pib;

public class X38TestingPibContextLoader extends X38TestingPibWebContextLoader {

	public X38TestingPibContextLoader() {
		super("test-integration/x38TestingPibWar", false);
	}

}