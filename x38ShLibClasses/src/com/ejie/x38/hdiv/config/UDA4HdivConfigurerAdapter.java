package com.ejie.x38.hdiv.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hdiv.config.annotation.ExclusionRegistry;
import org.hdiv.config.annotation.RuleRegistry;
import org.hdiv.ee.config.annotation.ValidationConfigurer;
import org.hdiv.ee.validator.ValidationTargetType;
import org.hdiv.listener.InitListener;
import org.hdiv.services.LinkProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.Link;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.ejie.x38.hdiv.aspect.LinkResourcesAspect;
import com.ejie.x38.hdiv.controller.utils.DinamicLinkProvider;
import com.ejie.x38.hdiv.controller.utils.MethodLinkDiscoverer;
import com.ejie.x38.hdiv.controller.utils.MethodMappingDiscoverer;
import com.ejie.x38.hdiv.controller.utils.UDASecureResourceProcesor;
import com.hdivsecurity.services.config.EnableHdiv4ServicesSecurityConfiguration.SupportedValidators;
import com.hdivsecurity.services.config.HdivServicesSecurityConfigurerAdapter;
import com.hdivsecurity.services.config.ServicesConfig.IdProtectionType;
import com.hdivsecurity.services.config.ServicesConfig.ServerSideHypermedia;
import com.hdivsecurity.services.config.ServicesSecurityConfigBuilder;

public abstract class UDA4HdivConfigurerAdapter extends HdivServicesSecurityConfigurerAdapter {

	@Autowired
	private RequestMappingHandlerMapping handler;

	@Bean
	public InitListener initListener() {
		return new InitListener();
	}

	protected abstract String getHomePage();

	protected abstract String getLoginPage();
	
	protected abstract String getDashboardUser();
	
	protected abstract String getDashboardPass();

	@Override
	public final void configure(final ServicesSecurityConfigBuilder builder) {

		builder.confidentiality(false).sessionExpired().homePage(getHomePage()).loginPage(getLoginPage());
		builder.showErrorPageOnEditableValidation(true);
		builder.cookiesIntegrity(false);
		builder.cookiesConfidentiality(false);
		builder.idProtection(IdProtectionType.PLAINTEXT_HID);
		builder.serverSideHypermedia(ServerSideHypermedia.COMPLETE);
		builder.dashboardUser(getDashboardUser()).dashboardPass(getDashboardPass());

		builder.validationSupport(SupportedValidators.JSONFORM, SupportedValidators.HTMLFORM);

		builder.allowPartialSubEntities(true);
	}

	public abstract void addCustomExclusions(final ExclusionRegistry registry);

	@Override
	public final void addExclusions(final ExclusionRegistry registry) {
		registry.addUrlExclusions("/scripts/.*", "/styles/.*", "/fonts/.*", "/error", "/*.gif");
		registry.addUrlExclusions("", getHomePage(), getLoginPage()).method("GET");
		registry.addUrlExclusions("/audit").method("POST");
		registry.addParamExclusions("hdiv-http-method");
		registry.addUrlExclusions("/.*/search");
		addCustomExclusions(registry);
	}

	public abstract void addCustomRules(final RuleRegistry registry);

	@Override
	public final void addRules(final RuleRegistry registry) {
		registry.addRule("numeric").acceptedPattern("^[0-9]+$");
		registry.addRule("text").acceptedPattern("[a-zA-Z0-9@.\\-_~]*$").rejectedPattern("(\\s|\\S)*(--)(\\s|\\S)*");
		registry.addRule("fulltext").acceptedPattern("[a-zA-Z0-9@.\\-_\\:~]*$").rejectedPattern("(\\s|\\S)*(--)(\\s|\\S)*");
		registry.addRule("valueList").acceptedPattern("\\[([^()])*\\]");
		registry.addRule("url").acceptedPattern("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
		registry.addRule("partialUrl").acceptedPattern("[a-zA-Z0-9@.\\-_\\/]*$").rejectedPattern("(\\s|\\S)*(--)(\\s|\\S)*");
		registry.addRule("boolean").acceptedPattern("(\\W|^)(true|false)(\\W|$)");
		registry.addRule("order").acceptedPattern("(\\W|^)(asc|desc)(\\W|$)");
		registry.addRule("locale").acceptedPattern("(\\W|^)(es|eu|en|fr)(\\W|$)");

		addCustomRules(registry);
	}

	public void customConfigureEditableValidation(final ValidationConfigurer validationConfigurer) {

	}

	@Override
	public final void configureEditableValidation(final ValidationConfigurer validationConfigurer) {

		validationConfigurer.addValidation("/.*")
				.forParameters("multiselection.selectedIds", "multiselection.lastSelectedId", "multiselection.deselectedIds", "seeker.selectedIds").rules("fulltext")
				.target(ValidationTargetType.CLIENT_PARAMETERS).disableDefaults();

		validationConfigurer.addValidation("/.*")
				.forParameters("columns\\[.*", "order\\[.*", "search.value", "search.regex", "start",
						"multiselection.selectedRowsPerPage\\[.*", "multiselection.*", "multiselection.accion",
						"multiselection.internalFeedback.*", "multiselection.internalFeedback.msgFeedBack")
				.rules("text").target(ValidationTargetType.CLIENT_PARAMETERS).disableDefaults();

		validationConfigurer
				.addValidation("/.*").forParameters("search.autosearch", "search.defaultSearchInfoCol.editable",
						"search.defaultSearchInfoCol.search", "search.searchOnEnter")
				.rules("boolean").target(ValidationTargetType.CLIENT_PARAMETERS);
		validationConfigurer.addValidation("/.*")
				.forParameters("search.defaultSearchInfoCol.name", "search.defaultSearchInfoCol.index", "search.defaultSearchInfoCol.width",
						"search.validate.rules..*", "sidx", "core.pkToken", "core.pkNames")
				.rules("text").target(ValidationTargetType.CLIENT_PARAMETERS);
		validationConfigurer.addValidation("/.*").forParameters("_", "nd", "rows", "page", "search.transitionConfig.duration", "draw",
				"length", "multiselection.numSelected").rules("numeric").target(ValidationTargetType.CLIENT_PARAMETERS);
		validationConfigurer.addValidation("/.*").forParameters("search.url").rules("partialUrl")
				.target(ValidationTargetType.CLIENT_PARAMETERS);

		validationConfigurer.addValidation("/.*").forParameters("sord").rules("order").target(ValidationTargetType.CLIENT_PARAMETERS);
		validationConfigurer.addValidation("/.*").forParameters("columns").rules("valueList").target(ValidationTargetType.CLIENT_PARAMETERS)
				.disableDefaults();

		validationConfigurer.addValidation(getLoginPage()).forParameters("userNames").disableDefaults();
		
		validationConfigurer.addValidation("/.*").forParameters("locale").rules("locale").target(ValidationTargetType.CLIENT_PARAMETERS);

		customConfigureEditableValidation(validationConfigurer);

		validationConfigurer.addValidation("/.*");

	}

	protected abstract List<Link> getStaticLinks();

	@Bean
	public DinamicLinkProvider linkProvider() {
		DinamicLinkProvider dinamicLinkProvider = new DinamicLinkProvider(getStaticLinks());
		return dinamicLinkProvider;
	};

	@Bean
	public List<LinkProvider> linkProviders() {
		return Arrays.asList((LinkProvider) linkProvider());
	};

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

		return mld;
	};

	@Bean
	public LinkResourcesAspect LinkResourcesAspect() {
		return new LinkResourcesAspect();
	}

}
