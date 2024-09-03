package ray.lee.common.pojo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ray.lee.common.pojo.BasePojo;

@Schema(description = "成功取得Token所回傳的物件")
@Data
public class ApiTokenResponse extends BasePojo {
	@Schema(description = "Token")
	private String token;
	@Schema(description = "Token有效期，預設3600秒")
	private long expiresIn;
}
