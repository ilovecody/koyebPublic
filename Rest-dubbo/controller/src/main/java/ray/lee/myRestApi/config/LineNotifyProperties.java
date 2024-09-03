package ray.lee.myRestApi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "ray.lee.linenotify")
public class LineNotifyProperties {
	private String redirect_uri;
	private String client_id;
	private String client_secret;
	private String gas_line_notify;
}
