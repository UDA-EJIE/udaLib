package com.ejie.x38.test.integration.config.portal;

/**
 * @author Eurohelp S.L.
 */
public class X38TestingPortalContextLoader extends X38TestingPortalWebContextLoader {

	public X38TestingPortalContextLoader() {
		super("test-integration/x38TestingPortalWar", false);
	}

}