package com.ejie.x38.hdiv.controller.model;

public interface LinkPredicate<T> {

	boolean test(final LinkInfo<T> linkInfo);
}
