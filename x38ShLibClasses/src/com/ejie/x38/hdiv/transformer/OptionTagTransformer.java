package com.ejie.x38.hdiv.transformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.bytecode.DuplicateMemberException;

@Component
public class OptionTagTransformer implements ClassTransformer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OptionTagTransformer.class);
	
	@Override
	public void transform() {
		try {
			ClassPool classPool = ClassPool.getDefault();
			Class<?> tag = Class.forName("org.springframework.web.servlet.tags.form.OptionTag");
			classPool.appendClassPath(new LoaderClassPath(tag.getClassLoader()));
			CtClass ctClass = classPool.get("org.springframework.web.servlet.tags.form.OptionTag");
			if (!ctClass.isFrozen()) {
				CtMethod modifyedMethod = ctClass.getDeclaredMethod("assertUnderSelectTag");
				modifyedMethod.setBody("{}");
				ctClass.toClass();
				LOGGER.info("OptionTagTransformer transformed");
			}else {
				LOGGER.info("OptionTagTransformer transformed already");
			}
			
		} catch (DuplicateMemberException e) {
			LOGGER.debug("Cannot transform class OptionTagTransformer. ", e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Cannot transform class OptionTagTransformer. ", e);
		}
	}
	
}
