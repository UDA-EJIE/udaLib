package com.ejie.x38.hdiv.controller.model;

import javax.servlet.http.HttpServletRequest;

public class LinkInfo<T> {

	private final String linkId;

	private final T entity;

	private final HttpServletRequest request;

	public LinkInfo(final String linkId, final T entity, final HttpServletRequest request) {
		this.linkId = linkId;
		this.entity = entity;
		this.request = request;
	}

	public String getLinkId() {
		return linkId;
	}

	public T getEntity() {
		return entity;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

}
