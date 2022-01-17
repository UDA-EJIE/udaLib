package com.ejie.x38.hdiv.controller.utils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hdiv.services.LinkProvider;
import org.springframework.hateoas.Link;

public class DinamicLinkProvider implements LinkProvider<Link> {

	private final Map<String, Set<Link>> linksMap = new HashMap<String, Set<Link>>();

	private final List<Link> staticLinks;

	public DinamicLinkProvider(final List<Link> staticLinks) {
		if (staticLinks == null) {
			this.staticLinks = new ArrayList<Link>();
		}
		else {
			this.staticLinks = staticLinks;
		}

	}

	@Override
	public List<Link> getLinks(final String arg0, final Principal arg1, final HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session != null) {
			Set<Link> sessionLinks = linksMap.get(session.getId());
			if (sessionLinks != null) {
				sessionLinks.addAll(staticLinks);
				return new ArrayList<Link>(sessionLinks);
			}
		}
		return staticLinks;
	}

	public boolean addLinks(final List<Link> links, final HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Set<Link> stored = linksMap.get(session.getId());
			if (stored == null) {
				stored = new HashSet<Link>();
			}
			stored.addAll(links);
			linksMap.put(session.getId(), stored);
			return true;
		}
		return false;
	}

}
