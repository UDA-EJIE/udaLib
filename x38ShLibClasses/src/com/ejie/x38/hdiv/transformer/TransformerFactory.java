package com.ejie.x38.hdiv.transformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformerFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransformerFactory.class);
	
	private final List<ClassTransformer> transformers;

	public TransformerFactory(final Collection<ClassTransformer> transformers) {
		this.transformers = new ArrayList<ClassTransformer>(transformers);
	}

	public TransformerFactory doTransform() {
		
		LOGGER.info("******** TransformerFactory doTransform classes " + transformers.size());
		
		for(ClassTransformer classTransformer : transformers) {
			
			LOGGER.info("******** TransformerFactory doTransform -- " + classTransformer.getClass());
			classTransformer.transform();
		}
		return this;
	}
}
