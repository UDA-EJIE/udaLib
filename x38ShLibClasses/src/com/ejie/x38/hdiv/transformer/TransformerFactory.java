package com.ejie.x38.hdiv.transformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TransformerFactory {

	private final List<ClassTransformer> transformers;

	public TransformerFactory(final Collection<ClassTransformer> transformers) {
		this.transformers = new ArrayList<ClassTransformer>(transformers);
	}

	public TransformerFactory doTransform() {
		for(ClassTransformer classTransformer : transformers) {
			classTransformer.transform();
		}
		return this;
	}
}
