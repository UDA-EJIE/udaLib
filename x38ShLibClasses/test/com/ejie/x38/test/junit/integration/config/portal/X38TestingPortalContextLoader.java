package com.ejie.x38.test.junit.integration.config.portal;

/**
 * @author Eurohelp S.L.
 */
public class X38TestingPortalContextLoader extends X38TestingPortalWebContextLoader {

	public X38TestingPortalContextLoader() {
		super("test/x38TestingPortalWar", false);
	}

}