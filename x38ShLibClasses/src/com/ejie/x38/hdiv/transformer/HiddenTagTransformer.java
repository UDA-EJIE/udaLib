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
public class HiddenTagTransformer implements ClassTransformer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HiddenTagTransformer.class);
	
	@Override
	public void transform() {
		try {
			ClassPool classPool = ClassPool.getDefault();
			Class<?> tag = Class.forName("org.springframework.web.servlet.tags.form.HiddenInputTag");
			classPool.appendClassPath(new LoaderClassPath(tag.getClassLoader()));
			CtClass ctClass = classPool.get("org.springframework.web.servlet.tags.form.HiddenInputTag");
			if (!ctClass.isFrozen()) {
				String strMethod = " public void setDynamicAttribute( String uri, String localName, Object value) throws javax.servlet.jsp.JspException {" //
									+ "		org.springframework.web.servlet.support.RequestContext ctx = (org.springframework.web.servlet.support.RequestContext) this.pageContext.getAttribute(REQUEST_CONTEXT_PAGE_ATTRIBUTE);"//
									+ "		if(\"value\".equals(localName)) {"//
									+ "			if (ctx != null) {"//
									+ "				org.springframework.web.servlet.support.RequestDataValueProcessor processor = ctx.getRequestDataValueProcessor();"//
									+ "				javax.servlet.ServletRequest request = this.pageContext.getRequest();"//
									+ "				if (processor != null && (request instanceof javax.servlet.http.HttpServletRequest)) {"//
									+ "					processor.processFormFieldValue((javax.servlet.http.HttpServletRequest) request, getPath(), String.valueOf(value), \"hidden\");"//
									+ "				}"//
									+ "			}"//
									+ "		}"//
									+ "		super.setDynamicAttribute(uri, localName, value);"//
									+ "	}";
				
				CtMethod newmethod = CtNewMethod.make(strMethod,ctClass);
				ctClass.addMethod(newmethod);
				ctClass.toClass();
				LOGGER.info("HiddenTagTransformer transformed");
			}else {
				LOGGER.info("HiddenTagTransformer transformed already");
			}
			
		}catch(Exception e ) {
			LOGGER.error("Cannot transform class HiddenInputTag. ", e);
		}
	}
	
}
