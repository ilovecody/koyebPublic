package ray.lee.myRestApi.common.pojo.request;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ray.lee.myRestApi.base.components.BasePojo;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OauthRequest extends BasePojo {
	@Parameter(description = "for demo可直接填入D2EC92A8FAE94E9D")
	@NotBlank(message = "OauthRequest.clientId.NotBlank")
	private String clientId;
	@Parameter(description = "for demo可直接填入hao3p1hxv4gsk-uaevbzwk0wikw-wuk6pwjou570dau")
	@NotBlank(message = "OauthRequest.clientSecret.NotBlank")
	private String clientSecret;
}
