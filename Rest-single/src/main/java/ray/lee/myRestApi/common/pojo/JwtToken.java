package ray.lee.myRestApi.common.pojo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ray.lee.myRestApi.base.components.BasePojo;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtToken extends BasePojo {
	private long timestamp;
	private String clientId;
	private String token;
	
	public JwtToken(String json) {
		super.constructorFromJsonString(json, this.getClass());
	}
}
