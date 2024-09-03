package ray.lee.myRestApi.common.config;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import ray.lee.myRestApi.common.utilities.i18NUtil;

@Configuration
public class I18nConfig {
	
	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
		source.setBasenames("classpath:static/i18n/restApiMessage/restapi_message",
							"classpath:static/i18n/restApiMessage/param_validation");
		source.setDefaultEncoding(StandardCharsets.UTF_8.name());
		source.setCacheSeconds(1);
		return source;
	}
}
