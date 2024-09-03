package ray.lee.utilities;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class i18NUtil {
		public static Locale DEFAULT_LOCALE = Locale.US;
		private static ReloadableResourceBundleMessageSource bundle;
		
		@Autowired
		public void setBundle(ReloadableResourceBundleMessageSource bundle) {
			this.bundle = bundle;
		}
		
		public static String getMessage(String code, @Nullable Object... args) {
				return getMessage(code, args, DEFAULT_LOCALE);
		}

		public static String getMessage(String code, @Nullable Object[] args, Locale locale) {
			  	try {
			  		return bundle.getMessage(code, args, locale);
			  	}catch(Exception e) {
			  		 log.debug("Failed to load resource bundle code={}, {}", code, e.getMessage());
			  	}
			  	return "Error message not found.";
		}
}
