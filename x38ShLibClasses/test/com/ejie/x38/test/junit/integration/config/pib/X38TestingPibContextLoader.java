package com.ejie.x38.test.junit.integration.config.pib;

public class X38TestingPibContextLoader extends X38TestingPibWebContextLoader {

	public X38TestingPibContextLoader() {
		super("test/x38TestingPibWar", false);
	}

}