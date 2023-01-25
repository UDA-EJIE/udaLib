package com.ejie.x38.hdiv.transformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;

@Component
public class SelectTagTransformer implements ClassTransformer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SelectTagTransformer.class);
	
	private void tryTransform() throws Exception {
		ClassPool classPool = ClassPool.getDefault();
		Class<?> tag = Class.forName("org.springframework.web.servlet.tags.form.SelectTag");
		classPool.appendClassPath(new LoaderClassPath(tag.getClassLoader()));
		
		loadClass(classPool, "org.springframework.web.servlet.tags.form.ValueFormatter");
		loadClass(classPool, "org.springframework.web.servlet.tags.form.OptionWriter");
		
		loadClass(classPool, "org.springframework.web.servlet.tags.form.SelectedValueComparator");
		loadClass(classPool, "org.springframework.web.servlet.tags.form.SelectOptionWriter");
		
		CtClass ctClass = classPool.get("org.springframework.web.servlet.tags.form.SelectTag");
		if (!ctClass.isFrozen()) {
			
			String strMethod = " protected String getName() throws javax.servlet.jsp.JspException {" //
						  	 + "	return super.getName();"//
						  	 + " }";

			CtMethod newmethod = CtNewMethod.make(strMethod,ctClass);
			ctClass.addMethod(newmethod);
	
			CtMethod modifyedMethod = ctClass.getDeclaredMethod("writeTagContent");
			modifyedMethod.setBody("{"
								 + "    $1.startTag(\"select\");"
								 + "	writeDefaultAttributes($1);"
								 + "	if (isMultiple()) {"
								 + "		$1.writeAttribute(\"multiple\", \"multiple\");"
								 + "	}"
								 + "	$1.writeOptionalAttributeValue(\"size\", getDisplayString(evaluate(\"size\", getSize())));"
								 + "	Object items = getItems();"
								 + "	if (items != null) {"
								 + "		if (items != EMPTY) {"
								 + "			Object itemsObject = evaluate(\"items\", items);"
								 + "			if (itemsObject != null) {"
								 + "				final String selectName = $0.getName();"
								 + "				String valueProperty = (getItemValue() != null ?"
								 + "						org.springframework.util.ObjectUtils.getDisplayString(evaluate(\"itemValue\", getItemValue())) : null);"
								 + "				String labelProperty = (getItemLabel() != null ?"
								 + "						org.springframework.util.ObjectUtils.getDisplayString(evaluate(\"itemLabel\", getItemLabel())) : null);"
								 + "				org.springframework.web.servlet.tags.form.OptionWriter optionWriter = new org.springframework.web.servlet.tags.form.SelectOptionWriter(selectName, itemsObject, $0.getBindStatus(), valueProperty, labelProperty, isHtmlEscape());"
								 + "				optionWriter.writeOptions($1);"
								 + "			}"
								 + "		}"
								 + "		$1.endTag(true);"
								 + "		writeHiddenTagIfNecessary($1);"
								 + "		return SKIP_BODY;"
								 + "	}else {"
								 + "		processFieldValue($0.getName(), null, \"option\");"
								 + "		$1.forceBlock();"
								 + "		this.tagWriter = $1;"
								 + "		this.pageContext.setAttribute(LIST_VALUE_PAGE_ATTRIBUTE, $0.getBindStatus());"
								 + "		return EVAL_BODY_INCLUDE;"
								 + "	}"
								 + "} ");

			ctClass.toClass();
		}
	}
	
	private void loadClass(ClassPool classPool, String className) throws Exception {
		
		Class<?> tag = Class.forName(className);
		classPool.appendClassPath(new LoaderClassPath(tag.getClassLoader()));
		
		CtClass ctClass = classPool.get(className);
		
		CtClass[] innerClasses= ctClass.getDeclaredClasses();
		for (CtClass nested : innerClasses) {
		    //Adds support for anonymous/inner classes
			try {
				LOGGER.debug("Transform inner class " + nested.getName());
				nested.toClass();
			}catch(Exception e) {
				LOGGER.error("Exception transforming class " + nested.getName(), e);
			}
		}
		ctClass.toClass();
	}
	
	@Override
	public void transform() {
		
		try {
			tryTransform();
		}catch(Exception e ) {
			LOGGER.error("Cannot transform class SelectTagTransformer. ", e);
		}
	}
	
}
