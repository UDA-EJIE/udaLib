package com.ejie.x38.hdiv.datacomposer;

import org.hdiv.context.RequestContextHolder;
import org.hdiv.dataComposer.DataComposerMemory;
import org.hdiv.state.IParameter;
import org.hdiv.state.IState;

public class EjieDataComposerMemory extends DataComposerMemory {
	
	public EjieDataComposerMemory(RequestContextHolder requestContext) {
		super(requestContext);
	}
	
	public String resetAndCompose(String parameterName, String value, boolean editable) {
		IState state = getStates().peek();

		IParameter parameter = state.getParameter(parameterName);
		try {
			parameter.getValues().clear();
		} catch (Exception e) {
			// No action needed.
		}
		
		return super.compose(parameterName, value, editable);
	}	
}
