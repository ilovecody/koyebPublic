package ray.lee.myRestApi.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import ray.lee.common.components.RequestBodyValidationGroup.*;
import ray.lee.common.constants.MessageCode;
import ray.lee.common.exceptions.MyRestApiException;
import ray.lee.common.pojo.request.ListOrderRequest;
import ray.lee.common.pojo.request.OrderRequest;
import ray.lee.common.pojo.response.ListOrderResponse;
import ray.lee.common.pojo.response.OrderResponse;
import ray.lee.common.service.ServerContext;
import ray.lee.common.service.Enum.ActionId;
import ray.lee.common.service.Enum.ServiceId;
import ray.lee.model.Enum.UserRole;
import ray.lee.myRestApi.aop.annotation.PermissionCheck;
import ray.lee.myRestApi.aop.annotation.RestapiLimitationCheck;
import ray.lee.myRestApi.common.components.BaseController;
import ray.lee.utilities.i18NUtil;

@Tag(name = "2. Order", description = "訂單相關操作")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({@ApiResponse(responseCode = "400", ref = "#/components/responses/400"),
			   @ApiResponse(responseCode = "403", ref = "#/components/responses/403")})
@Controller
@RequestMapping("/order")
@RestapiLimitationCheck()
public class OrderController extends BaseController {
	@Resource
	private HttpServletRequest request;
	
	@Operation(summary = "新增訂單", 
			   requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MimeTypeUtils.APPLICATION_JSON_VALUE,
					   																				  examples = @ExampleObject("{\"productId\": 1,\"quantity\": 2,\"totalAmount\": 100}"))),
			   responses = {@ApiResponse(responseCode = "201", description = "新增完成",
							 			 content = @Content(mediaType = MimeTypeUtils.APPLICATION_JSON_VALUE,
							 			 					schema = @Schema(implementation = OrderResponse.class)))})	
	@PermissionCheck(role = {UserRole.Admin})
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping()
	@ResponseBody
	public OrderResponse createOrder(@RequestBody @Validated(Post.class) OrderRequest order) throws Exception {
		ServerContext ctx= new ServerContext();
		ctx.setServiceId(ServiceId.orderService);
		ctx.setActionId(ActionId.order_create);
		ctx.setRequestParameter(order);
		ctx.setUser(super.getUserCredential(request));
		ctx = super.doService(ctx);
		
		if(ctx.getReturnMessage().isSuccess()) {
			return ctx.getResponseResult(OrderResponse.class);
		} else {
			super.doServiceFailureResponse(ctx.getReturnMessage().getCode());
			return null;
		}
	}
	
	@Operation(summary = "依據分頁取回訂單列表",
			   responses = @ApiResponse(responseCode = "200", description = "成功",
					   					content = @Content(mediaType = MimeTypeUtils.APPLICATION_JSON_VALUE,
					   									   schema = @Schema(implementation = ListOrderResponse.class))))
	@PermissionCheck(role = {UserRole.Admin})
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/list")
	@ResponseBody
	//public ListOrderResponse listOrders(@RequestBody @Validated(Get.class) ListOrderRequest pageableRequest) throws Exception {
	//若使用swagger,則Get method不允許使用@RequestBody方式帶入參數,無法在http request body內使用josn帶入參數,只能用url?param=xx
	public ListOrderResponse listOrders(@ParameterObject @Validated(Get.class) ListOrderRequest pageableRequest) throws Exception {
		ServerContext ctx= new ServerContext();
		ctx.setServiceId(ServiceId.orderService);
		ctx.setActionId(ActionId.order_listOrders);
		ctx.setRequestParameter(pageableRequest);
		ctx.setUser(super.getUserCredential(request));
		ctx = super.doService(ctx);
		
		if(ctx.getReturnMessage().isSuccess()) {
			return ctx.getResponseResult(ListOrderResponse.class);
		} else {
			super.doServiceFailureResponse(ctx.getReturnMessage().getCode());
			return null;
		}
	}
	
	@Operation(summary = "依據orderId取回訂單資料",
			   responses = {@ApiResponse(responseCode = "200", description = "成功",
					   					 content = @Content(mediaType = MimeTypeUtils.APPLICATION_JSON_VALUE,
					   									    schema = @Schema(implementation = OrderResponse.class))),
					   		@ApiResponse(responseCode = "404", description = "訂單不存在", content = @Content(schema = @Schema(hidden = true)))})
	@PermissionCheck(role = {UserRole.Admin})
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{orderId}")
	@ResponseBody
	public OrderResponse getOrderById(@PathVariable @Min(1) int orderId) throws Exception {
		ServerContext ctx= new ServerContext();
		ctx.setServiceId(ServiceId.orderService);
		ctx.setActionId(ActionId.order_getByOrderId);
		ctx.setRequestParameter(orderId);
		ctx = super.doService(ctx);
		
		if(ctx.getReturnMessage().isSuccess()) {
			return ctx.getResponseResult(OrderResponse.class);
		} else {
			super.doServiceFailureResponse(ctx.getReturnMessage().getCode());
			return null;
		}
	}	
	
	//多個欄位或整個Object or Entity更新
	@Operation(summary = "修改訂單資料",
			   responses = {@ApiResponse(responseCode = "204", ref = "#/components/responses/204"),
					   		@ApiResponse(responseCode = "404", description = "訂單不存在", content = @Content(schema = @Schema(hidden = true)))})
	@PermissionCheck(role = {UserRole.Admin})
	@ResponseStatus(HttpStatus.NO_CONTENT)	
	@PutMapping()
	public void updateOrder(@RequestBody @Validated(Put.class)OrderRequest order) throws Exception {
		ServerContext ctx= new ServerContext();
		ctx.setServiceId(ServiceId.orderService);
		ctx.setActionId(ActionId.order_update);
		ctx.setRequestParameter(order);
		ctx = super.doService(ctx);
		
		if(!ctx.getReturnMessage().isSuccess()) {
			super.doServiceFailureResponse(ctx.getReturnMessage().getCode());
		}
	}
	
	//局部更新某些欄位
	@Operation(summary = "修改訂單的數量",
			   responses = {@ApiResponse(responseCode = "204", ref = "#/components/responses/204"),
				   		    @ApiResponse(responseCode = "404", description = "訂單不存在", content = @Content(schema = @Schema(hidden = true)))})
	@PermissionCheck(role = {UserRole.Admin})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PatchMapping("/{orderId}/quantity/{quantity}")
	public void updateOrderQuantity(@PathVariable @Min(1) int orderId, @PathVariable @Min(1) int quantity) throws Exception {
		//可另外自訂驗證器or工具,統一處理errorCode and message,使用aop之類
		if(quantity < 1) {
			String msgCode = this.getClass().getSimpleName()+".updateOrderQuantity.Min";
			throw new MyRestApiException(MessageCode.Param_Validation_Error, i18NUtil.getMessage(msgCode));
		}
		
		ServerContext ctx= new ServerContext();
		ctx.setServiceId(ServiceId.orderService);
		ctx.setActionId(ActionId.order_updateQuantity);
		ctx.setRequestParameter(OrderRequest.builder()
											.orderId(orderId)
											.quantity(quantity)
											.build());
		ctx = super.doService(ctx);		
		
		if(!ctx.getReturnMessage().isSuccess()) {
			super.doServiceFailureResponse(ctx.getReturnMessage().getCode());
		}
	}
	
	@Operation(summary = "依據orderId刪除訂單",
			   responses = {@ApiResponse(responseCode = "204", ref = "#/components/responses/204"),
			   		        @ApiResponse(responseCode = "404", description = "訂單不存在", content = @Content(schema = @Schema(hidden = true)))})
	@PermissionCheck(role = {UserRole.Admin})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{orderId}")
	public void deleteOrder(@PathVariable @Min(1) int orderId) throws Exception {
		ServerContext ctx= new ServerContext();
		ctx.setServiceId(ServiceId.orderService);
		ctx.setActionId(ActionId.order_delete);
		ctx.setRequestParameter(orderId);
		ctx = super.doService(ctx);
		
		if(!ctx.getReturnMessage().isSuccess()) {
			super.doServiceFailureResponse(ctx.getReturnMessage().getCode());
		}
	}
}
