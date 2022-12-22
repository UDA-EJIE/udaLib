package com.ejie.x38.hdiv.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.hdiv.config.HDIVConfig;
import org.hdiv.config.annotation.EnableHdivWebSecurity;
import org.hdiv.config.annotation.ExclusionRegistry;
import org.hdiv.config.annotation.LongLivingPagesRegistry;
import org.hdiv.config.annotation.RuleRegistry;
import org.hdiv.config.annotation.ValidationConfigurer;
import org.hdiv.config.annotation.builders.SecurityConfigBuilder;
import org.hdiv.config.annotation.configuration.HdivWebSecurityConfigurer;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataValidator.IDataValidator;
import org.hdiv.filter.IValidationHelper;
import org.hdiv.filter.ValidatorErrorHandler;
import org.hdiv.filter.ValidatorHelperRequest;
import org.hdiv.idGenerator.UidGenerator;
import org.hdiv.listener.InitListener;
import org.hdiv.services.CustomSecureConverter;
import org.hdiv.services.CustomSecureSerializer;
import org.hdiv.session.ISession;
import org.hdiv.state.StateUtil;
import org.hdiv.state.scope.StateScopeManager;
import org.hdiv.urlProcessor.BasicUrlProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.ejie.x38.hdiv.aspect.LinkResourcesAspect;
import com.ejie.x38.hdiv.config.EjieValidationConfigurer.EjieValidationConfig.EjieEditableValidationConfigurer;
import com.ejie.x38.hdiv.controller.utils.MethodLinkDiscoverer;
import com.ejie.x38.hdiv.controller.utils.MethodMappingDiscoverer;
import com.ejie.x38.hdiv.datacomposer.EjieDataComposerFactory;
import com.ejie.x38.hdiv.error.EjieValidationErrorHander;
import com.ejie.x38.hdiv.filter.EjieValidatorHelperRequest;
import com.ejie.x38.hdiv.processor.UDASecureResourceProcesor;
import com.ejie.x38.hdiv.protection.IdProtectionDataManager;
import com.ejie.x38.hdiv.protection.UserSessionIdProtectionDataManager;
import com.ejie.x38.hdiv.transformer.ClassTransformer;
import com.ejie.x38.hdiv.transformer.TransformerFactory;
import com.ejie.x38.hdiv.util.Constants;
import com.ejie.x38.serialization.EjieSecureModule;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;

@ComponentScan(basePackages = "com.ejie.x38.hdiv")
@EnableHdivWebSecurity
public abstract class UDA4HdivConfigurerAdapter implements HdivWebSecurityConfigurer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UDA4HdivConfigurerAdapter.class);
	
	@Autowired
	private RequestMappingHandlerMapping handler;
	
	@Autowired
	private StateUtil stateUtil;

	@Autowired
	private HDIVConfig config;

	@Autowired
	private ISession session;

	@Autowired
	private IDataValidator dataValidator;

	@Autowired
	private BasicUrlProcessor basicUrlProcessor;

	@Autowired
	private StateScopeManager stateScopeManager;
	
	@Autowired
	private ApplicationContext appContext;
	
	@Autowired
	private UidGenerator uidGenerator;
	
	@PostConstruct
	public void init() {
		configureSerializer();
	}
	
	@Bean
	@Primary
	public DataComposerFactory dataComposerFactory() {
		DataComposerFactory dataComposerFactory = new EjieDataComposerFactory();
		dataComposerFactory.setConfig(config);
		dataComposerFactory.setSession(session);
		dataComposerFactory.setStateUtil(stateUtil);
		dataComposerFactory.setUidGenerator(uidGenerator);
		dataComposerFactory.setStateScopeManager(stateScopeManager);
		return dataComposerFactory;
	}
	
	@Bean
	@Primary
	public IValidationHelper ejieRequestValidationHelper() {

		ValidatorHelperRequest validatorHelperRequest = new EjieValidatorHelperRequest();
		validatorHelperRequest.setStateUtil(stateUtil);
		validatorHelperRequest.setHdivConfig(config);
		validatorHelperRequest.setSession(session);
		validatorHelperRequest.setDataValidator(dataValidator);
		validatorHelperRequest.setUrlProcessor(basicUrlProcessor);
		validatorHelperRequest.setDataComposerFactory(dataComposerFactory());
		validatorHelperRequest.setStateScopeManager(stateScopeManager);
		((EjieValidatorHelperRequest)validatorHelperRequest).setIdProtectionDataManager(idProtectionDataManager());
		validatorHelperRequest.init();
		return validatorHelperRequest;
	}
	
	@Bean
	public MethodMappingDiscoverer MethodMappingDiscoverer() {

		MethodMappingDiscoverer mpd = new MethodMappingDiscoverer(handler);

		Map<RequestMappingInfo, HandlerMethod> map = handler.getHandlerMethods();
		for (Entry<RequestMappingInfo, HandlerMethod> mapEntry : map.entrySet()) {
			mpd.addMethodMappings(mapEntry.getValue().getMethod().toString(), mapEntry.getKey().getPatternsCondition().getPatterns(),
					mapEntry.getKey().getMethodsCondition().getMethods(), mapEntry.getValue().getMethodParameters());
		}

		return mpd;
	};

	@Bean
	public MethodLinkDiscoverer MethodLinkDiscoverer() {

		MethodLinkDiscoverer mld = new MethodLinkDiscoverer();
		UDASecureResourceProcesor.registerMethodLinkDiscoverer(mld);
		UDASecureResourceProcesor.registerIdProtectionDataManager(idProtectionDataManager());
		
		return mld;
	};

	@Bean
	public LinkResourcesAspect LinkResourcesAspect() {
		return new LinkResourcesAspect();
	}
	
	@Bean
	public InitListener initListener() {
		return new InitListener();
	}

	@Bean
    public TransformerFactory transformerFactory(final ApplicationContext context) {
        return new TransformerFactory(context.getBeansOfType(ClassTransformer.class).values()).doTransform();
    }
	 
	protected abstract String getHomePage();

	protected abstract String getLoginPage();

	@Override
	public final void configure(final SecurityConfigBuilder builder) {
		builder.confidentiality(false).sessionExpired().homePage(getHomePage()).loginPage(getLoginPage());
		builder.showErrorPageOnEditableValidation(true);
		builder.cookiesIntegrity(false);
		builder.cookiesConfidentiality(false);
		
		String errorPage = getErrorPage();
		if(errorPage != null) {
			builder.errorPage(errorPage);
		}
	}
	
	protected String getErrorPage() {
		return null;
	}

	public abstract void addCustomExclusions(final ExclusionRegistry registry);

	@Override
	public final void addExclusions(final ExclusionRegistry registry) {
		registry.addUrlExclusions("/scripts/.*", "/styles/.*", "/fonts/.*", "/error", "/*.gif");
		registry.addUrlExclusions("", getHomePage(), getLoginPage(), "/hdiv-devtool").method("GET");
		registry.addUrlExclusions("/audit").method("POST");
		registry.addUrlExclusions("/.*/search");
		registry.addParamExclusions("_", "MODIFY_FORM_FIELD_NAME", "_MODIFY_HDIV_STATE_");
		addCustomExclusions(registry);
	}

	public abstract void addCustomRules(final RuleRegistry registry);

	@Override
	public final void addRules(final RuleRegistry registry) {
		registry.addRule("numeric").acceptedPattern("^[0-9]+$");
		registry.addRule("text").acceptedPattern("[a-zA-Z0-9@.\\-_~]*$").rejectedPattern("(\\s|\\S)*(--)(\\s|\\S)*");
		registry.addRule("textWhitespaces").acceptedPattern("[a-zA-Z0-9@.\\-_~ ]*$").rejectedPattern("(\\s|\\S)*(--)(\\s|\\S)*");
		registry.addRule("fulltext").acceptedPattern("[a-zA-Z0-9@.,\\-_\\:~\\$]*$").rejectedPattern("(\\s|\\S)*(--)(\\s|\\S)*");
		registry.addRule("fulltextWhitespaces").acceptedPattern("[a-zA-Z0-9@.,\\-_\\:~ ]*$").rejectedPattern("(\\s|\\S)*(--)(\\s|\\S)*");
		registry.addRule("valueList").acceptedPattern("\\[([^()])*\\]");
		registry.addRule("url").acceptedPattern("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
		registry.addRule("partialUrl").acceptedPattern("[a-zA-Z0-9@.\\-_\\/]*$").rejectedPattern("(\\s|\\S)*(--)(\\s|\\S)*");
		registry.addRule("boolean").acceptedPattern("(\\W|^)(true|false)(\\W|$)");
		registry.addRule("order").acceptedPattern("(\\W|^)(asc|desc| ,asc| ,desc)*(\\W|$)");
		registry.addRule("locale").acceptedPattern("(\\W|^)(es|eu|en|fr)(\\W|$)");
		registry.addRule("method").acceptedPattern("(\\W|^)(GET|HEAD|POST|PUT|DELETE|CONNECT|OPTIONS|TRACE|PATCH)(\\W|$)");
		
		//Custom rule to be used in 'modify' requests
		registry.addRule(Constants.MODIFY_RULE_NAME).rejectedPattern("-");
		
		addCustomRules(registry);
	}

	public void customConfigureEditableValidation(final ValidationConfigurer validationConfigurer) {

	}

	@Override
	public final void configureEditableValidation(ValidationConfigurer validationConfigurer) {
		
		EjieValidationConfigurer ejieValidationConfigurer = new EjieValidationConfigurer(validationConfigurer);
		
		((EjieEditableValidationConfigurer) ejieValidationConfigurer.addValidation("/.*")
				.forParameters("multiselection.selectedIds", "multiselection.lastSelectedId", "multiselection.deselectedIds", "seeker.selectedIds")
				.rules("fulltext"))
				.setAsClientParameter(true).disableDefaults();

		((EjieEditableValidationConfigurer) ejieValidationConfigurer.addValidation("/.*")
				.forParameters("columns.data", "columns.name", "columns\\[.*", "order\\[.*", "search.value", "search.regex", "start",
						"multiselection.selectedRowsPerPage\\[.*", "multiselection.*", "multiselection.accion",
						"multiselection.internalFeedback.*", "multiselection.internalFeedback.msgFeedBack")
				.rules("text")).setAsClientParameter(true).disableDefaults();

		((EjieEditableValidationConfigurer) ejieValidationConfigurer
				.addValidation("/.*").forParameters("columns.searchable", "columns.orderable", "search.autosearch", "search.defaultSearchInfoCol.editable",
						"search.defaultSearchInfoCol.search", "search.searchOnEnter", "columns.search.regex")
				.rules("boolean")).setAsClientParameter(true);
		
		((EjieEditableValidationConfigurer) ejieValidationConfigurer.addValidation("/.*")
				.forParameters("search.defaultSearchInfoCol.name", "search.defaultSearchInfoCol.index", "search.defaultSearchInfoCol.width",
						"search.validate.rules.*", "core.pkToken", "core.pkNames", "columns.search.value")
				.rules("text")).setAsClientParameter(true);
		
		((EjieEditableValidationConfigurer) ejieValidationConfigurer.addValidation("/.*").forParameters("_", "nd", "rows", "page", "search.transitionConfig.duration", "draw",
				"length", "multiselection.numSelected", "order.column").rules("numeric")).setAsClientParameter(true);
		
		((EjieEditableValidationConfigurer) ejieValidationConfigurer.addValidation("/.*").forParameters("search.url").rules("partialUrl"))
				.setAsClientParameter(true);

		((EjieEditableValidationConfigurer) ejieValidationConfigurer.addValidation("/.*").forParameters("sidx").rules("fulltextWhitespaces")).setAsClientParameter(true);
		((EjieEditableValidationConfigurer) ejieValidationConfigurer.addValidation("/.*").forParameters("sord", "order.dir").rules("order")).setAsClientParameter(true);
		((EjieEditableValidationConfigurer) ejieValidationConfigurer.addValidation("/.*").forParameters("columns").rules("valueList")).setAsClientParameter(true)
				.disableDefaults();

		ejieValidationConfigurer.addValidation(getLoginPage()).forParameters("userNames").disableDefaults();
		
		((EjieEditableValidationConfigurer) ejieValidationConfigurer.addValidation("/.*").forParameters("locale").rules("locale")).setAsClientParameter(true);

		customConfigureEditableValidation(ejieValidationConfigurer);

		ejieValidationConfigurer.addValidation("/.*");
		
		ejieValidationConfigurer.consolidate();
	
	}
	
	@Override
	public void addLongLivingPages(LongLivingPagesRegistry arg0) {
	}
	
	@Bean
	@Primary
	public ObjectMapper objectMapper() {
	    return new ObjectMapper().registerModule(new EjieSecureModule(idProtectionDataManager(), config));
	}
	
	@Bean 
	public IdProtectionDataManager idProtectionDataManager() {
		return new UserSessionIdProtectionDataManager();
	}
	
	@Bean
	@Primary
	public ValidatorErrorHandler validatorErrorHandler() {
		return new EjieValidationErrorHander(config);
	}
	
	private void configureSerializer() {
		try {
			Map<String, AbstractJackson2HttpMessageConverter> beans = appContext.getBeansOfType(AbstractJackson2HttpMessageConverter.class);
			processBeans(beans.values(), false);
			
			Map<String, CustomSecureSerializer> serialyzers = appContext.getBeansOfType(CustomSecureSerializer.class);
			processSerializers(serialyzers.values());
		}
		catch (Exception e) {
			LOGGER.error("Cannot configure serializers. ", e);
		}
	}

	private void processBeans(final Collection<? extends HttpMessageConverter<?>> beans, final boolean force) {
		List<AbstractJackson2HttpMessageConverter> candidates = new ArrayList<AbstractJackson2HttpMessageConverter>();

		try {
			for (HttpMessageConverter<?> c : beans) {
				if (c instanceof AbstractJackson2HttpMessageConverter) {
					AbstractJackson2HttpMessageConverter bean = (AbstractJackson2HttpMessageConverter) c;
					if (bean instanceof CustomSecureConverter) {
						for (ObjectMapper om : ((CustomSecureConverter) bean).getObjectMappers()) {
							doConfigureObjectMapper(om);
							candidates.add(bean);
						}
					}
					else {
						doConfigureObjectMapper(bean.getObjectMapper());
						candidates.add(bean);
					}
				}
			}
		}
		catch (Exception e) {
			LOGGER.error("Cannot process MessageConverter beans. ", e);
		}
	}
	
	private void processSerializers(final Collection<CustomSecureSerializer> serialyzers) {
		JsonSerializer<Object> secureSerializer = new EjieSecureModule.SecureIdSerializer();
		if (serialyzers != null) {
			for (CustomSecureSerializer customSecureSerializer : serialyzers) {
				customSecureSerializer.setDelegatedSerializer(secureSerializer);
			}
		}
	}	
	
	protected ObjectMapper doConfigureObjectMapper(final ObjectMapper objectMapper) {

		objectMapper.registerModule(new EjieSecureModule(idProtectionDataManager(), config));
		return objectMapper;
	}

}
