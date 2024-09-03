package ray.lee.myRestApi.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.MimeTypeUtils;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import ray.lee.myRestApi.common.pojo.response.ApiFailedResponse;

@OpenAPIDefinition(servers = @Server(url = "${ray.lee.swagger.server.url}"),
				   info = @Info(title = "REST API 簡易Demo", 
								version = "V1", 
								description = "<p><font color='red'>*因免費Server資源有限，每日會限制API次數</font></p>" +
											  "<p><font color='red'>*當次數已達限制時，回傳Http Status Code為429</font></p>" +
											  "<p><font color='red'>*API執行後，也可以直接進入DB查看執行結果&nbsp;&nbsp;</font>" + 
											  	  "(<a href='/db-info' target='_blank'>DB登入方式</a>)</p>" + 
											  "<p><a href='/h2-console' target='_blank'>點此進入DB</a>"))
@SecurityScheme(name = "bearerAuth",  
				type = SecuritySchemeType.HTTP,
				bearerFormat = "JWT",
				scheme = "bearer")
@Configuration
public class OpenApiConfig {
	
	@Bean
	public OpenAPI openApiComponents() {
		Schema apiFailedSchema = ModelConverters.getInstance()
								 .resolveAsResolvedSchema(new AnnotatedType(ApiFailedResponse.class)).schema;
		Content apiFailedContent = new Content().addMediaType(MimeTypeUtils.APPLICATION_JSON_VALUE,
															  new MediaType().schema(apiFailedSchema));
		
		ApiResponse res204 = new ApiResponse().description("成功");
		ApiResponse res400 = new ApiResponse().description("參數錯誤").content(apiFailedContent);
		ApiResponse res403 = new ApiResponse().description("Token驗證失敗或權限不足").content(apiFailedContent);
		
		return new OpenAPI().components(new Components().addResponses("204", res204)
														.addResponses("400", res400)
														.addResponses("403", res403));						 
	}
}
