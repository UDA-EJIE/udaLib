package com.ejie.x38.test.junit.integration.config.wrong;

import com.ejie.x38.test.junit.integration.config.X38TestingWebContextLoader;

public class X38TestingWrongContextLoader extends X38TestingWebContextLoader {

	public X38TestingWrongContextLoader() {
		super("test/x38TestingWrongWar", false);
	}

}