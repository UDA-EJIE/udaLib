package com.ejie.x38.hdiv.transformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;

@Component
public class SelectTagTransformer implements ClassTransformer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SelectTagTransformer.class);
	
	@Override
	public void transform() {
		try {
			ClassPool classPool = ClassPool.getDefault();
			Class<?> tag = Class.forName("org.springframework.web.servlet.tags.form.SelectTag");
			classPool.appendClassPath(new LoaderClassPath(tag.getClassLoader()));
			CtClass ctClass = classPool.get("org.springframework.web.servlet.tags.form.SelectTag");
			if (ctClass.isFrozen()) {
				ctClass.defrost();
			}
			CtMethod modifyedMethod = ctClass.getDeclaredMethod("writeTagContent");
			modifyedMethod.insertBefore(" processFieldValue(getName(), null, \"option\"); ");
			ctClass.toClass();
		}catch(Exception e ) {
			LOGGER.error("Cannot transform class SelectTagTransformer. ", e);
		}
	}
	
}
