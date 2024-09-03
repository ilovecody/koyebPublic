package ray.lee.myRestApi.config;

import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import ray.lee.utilities.i18NUtil;

//i18NUtil只在controller層注入為bean
@Import(i18NUtil.class)
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
