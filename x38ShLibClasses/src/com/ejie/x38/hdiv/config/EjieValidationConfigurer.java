package com.ejie.x38.hdiv.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.hdiv.config.annotation.ValidationConfigurer;
import org.hdiv.config.annotation.ValidationConfigurer.ValidationConfig.EditableValidationConfigurer;

import com.ejie.x38.hdiv.util.Constants;

public class EjieValidationConfigurer extends ValidationConfigurer {
	
	private ValidationConfigurer validationConfigurer;
	
	public EjieValidationConfigurer(ValidationConfigurer validationConfigurer) {
		this.validationConfigurer = validationConfigurer;

	}
	
	private void addValidationConfig(EjieValidationConfig validationConfig) {
		getValidationConfigs().add(validationConfig);
	}
	
	@SuppressWarnings("unchecked")
	protected void consolidate() {
		List<ValidationConfig> validationConfigs;
		try {
			Method m = ValidationConfigurer.class.getDeclaredMethod("getValidationConfigs");
			m.setAccessible(true);
			validationConfigs = ((List<ValidationConfig>) m.invoke(validationConfigurer));
		}catch(Exception e) {
			validationConfigs = new ArrayList<ValidationConfig>();
		}
		validationConfigs.addAll(getValidationConfigs());
	}
	

	@Override
	public EditableValidationConfigurer addValidation() {
		EjieValidationConfig validationConfig = new EjieValidationConfig();
		addValidationConfig(validationConfig);
		return validationConfig.getEditableValidationConfigurer();
	}

	@Override
	public EditableValidationConfigurer addValidation(final String urlPattern) {

		EjieValidationConfig validationConfig = new EjieValidationConfig(urlPattern);
		addValidationConfig(validationConfig);
		return validationConfig.getEditableValidationConfigurer();
	}
	
	public class EjieValidationConfig extends ValidationConfig implements Observer {
		
		private final EjieEditableValidationConfigurer ejieEditableValidationConfigurer = new EjieEditableValidationConfigurer(this);
		
		private boolean clientParameter = false;
		
		public EjieValidationConfig() {
			super();
		}

		public EjieValidationConfig(final String urlPattern) {
			super(urlPattern);
		}
		
		protected EditableValidationConfigurer getEditableValidationConfigurer() {
			return ejieEditableValidationConfigurer;
		}
		
		public boolean isClientParameter() {
			return clientParameter;
		}
		
		@Override
		protected String getUrlPattern() {
			return isClientParameter() ? Constants.CLIENT_PARAMETERS+super.getUrlPattern():super.getUrlPattern();
		}

		@Override
		public void update(Observable o, Object value) {
			clientParameter = (Boolean)value;
		}
		
		public class EjieEditableValidationConfigurer extends EditableValidationConfigurer {
			
			private boolean clientParameter = false;
			private Observer observer;
			
			public EjieEditableValidationConfigurer(Observer observer) {
				super();
				this.observer = observer;
			}
			
			public EditableValidationConfigurer setAsClientParameter(boolean isClient) {
				clientParameter = isClient;
				if(clientParameter) {
					observer.update(null, true);
				}
				return this;
			}
			
			public boolean isClientParameter() {
				return clientParameter;
			}
			
			public EditableValidationConfigurer setModifyParameter(String parameter) {
				//for(String modifierParam : getParameters()) {
				List<String> params = getParameters();
				for(int i = 0; i < params.size(); i++) {
					String modifierParam = params.get(i);
					params.remove(i);
					params.add(i, Constants.MODIFIER_PARAMETER + modifierParam);
				}
				params.add(Constants.MODIFY_TARGET_PARAMETER + parameter);
				rules(Constants.MODIFY_RULE_NAME);
				return this;
			}
			
		}
		
	}

}
