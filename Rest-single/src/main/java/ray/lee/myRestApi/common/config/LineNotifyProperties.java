package ray.lee.myRestApi.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ray.lee.linenotify")
public class LineNotifyProperties {
	private String redirect_uri;
	private String client_id;
	private String client_secret;
	private String gas_line_notify;
	
	public String getRedirect_uri() {
		return redirect_uri;
	}
	public void setRedirect_uri(String redirect_uri) {
		this.redirect_uri = redirect_uri;
	}
	public String getClient_id() {
		return client_id;
	}
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	public String getClient_secret() {
		return client_secret;
	}
	public void setClient_secret(String client_secret) {
		this.client_secret = client_secret;
	}
	public String getGas_line_notify() {
		return gas_line_notify;
	}
	public void setGas_line_notify(String gas_line_notify) {
		this.gas_line_notify = gas_line_notify;
	}	
}
