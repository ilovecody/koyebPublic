package ray.lee.common.pojo.response;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import org.springframework.http.HttpMethod;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ray.lee.common.pojo.BasePojo;
import ray.lee.utilities.i18NUtil;

@Schema(description = "API失敗時回傳的物件")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiFailedResponse extends BasePojo {
	@Schema(description = "API執行時間(UTC+8)", format = "date-time")
	private String timestamp;
	@Schema(description = "錯誤代碼")
	private String errorCode;
	@Schema(description = "錯誤訊息")
	private String errorMessage;
	@Schema(description = "API執行路徑")
	private String path;
	@Schema(description = "API執行的Http Method")
	private String httpMethod;
	/*
	public void setTimestamp(LocalDateTime date) {
		this.timestamp = date.toString()+OffsetDateTime.now().getOffset().toString();
		//this.timestamp = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))+OffsetDateTime.now().getOffset().toString();
	}
	*/
	
	public static ApiFailedResponse buildFailedResponse(String errorCode, String path, HttpMethod httpMethod, @Nullable Object... msgArgs) {
		return buildFailedResponse(errorCode, i18NUtil.getMessage(errorCode, msgArgs), path, httpMethod);
	}
	
	public static ApiFailedResponse buildFailedResponse(String errorCode, String errorMsg, String path, HttpMethod httpMethod) {
		String timestamp = LocalDateTime.now().toString()+OffsetDateTime.now().getOffset().toString();
		//timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))+OffsetDateTime.now().getOffset().toString();
		return new ApiFailedResponse(timestamp, errorCode, errorMsg, path, httpMethod.name());
	}
}
