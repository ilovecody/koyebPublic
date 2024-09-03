package ray.lee.myRestApi.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ray.lee.common.pojo.request.OauthRequest;
import ray.lee.common.pojo.response.ApiFailedResponse;
import ray.lee.common.pojo.response.ApiTokenResponse;
import ray.lee.common.service.ServerContext;
import ray.lee.common.service.Enum.ActionId;
import ray.lee.common.service.Enum.ServiceId;
import ray.lee.myRestApi.aop.annotation.RestapiLimitationCheck;
import ray.lee.myRestApi.common.components.BaseController;
import ray.lee.myRestApi.controller.utilities.MyRestConstants;

@Tag(name = "1. Token", description = "Token相關操作")
@Controller
@RequestMapping(MyRestConstants.oauthPath)
@RestapiLimitationCheck()
public class JwtLoginController extends BaseController {
	@Resource
	private HttpServletRequest request;
	
	@Operation(summary = "取得Bearer Token",
			   responses = {@ApiResponse(responseCode = "200", 
			   							 description = "成功取得Token", 
			   							 content = @Content(mediaType = MimeTypeUtils.APPLICATION_JSON_VALUE,
			   									 			schema = @Schema(implementation = ApiTokenResponse.class))),
					   		@ApiResponse(responseCode = "400",
					   					 description = "取得Token失敗",
					   					 content = @Content(mediaType = MimeTypeUtils.APPLICATION_JSON_VALUE,
					   										schema = @Schema(implementation = ApiFailedResponse.class)))})
	@GetMapping()
	@ResponseBody
	//public ApiTokenResponse getJwtToken(@RequestBody @Valid OauthRequest oauthRequest) throws Exception {
	//若使用swagger,則Get method不允許使用@RequestBody方式帶入參數,無法在http request body內使用josn帶入參數,只能用url?param=xx
	public ApiTokenResponse getJwtToken(@ParameterObject @Valid OauthRequest oauthRequest) throws Exception {
		ServerContext ctx= new ServerContext();
		ctx.setServiceId(ServiceId.jwtService);
		ctx.setActionId(ActionId.jwt_getToken);
		ctx.setRequestParameter(oauthRequest);
		
		ctx = super.doService(ctx);
		if(ctx.getReturnMessage().isSuccess()) {
			return ctx.getResponseResult(ApiTokenResponse.class);
		} else {
			super.doServiceFailureResponse(ctx.getReturnMessage().getCode());
			return null;
		}
	}	
}
