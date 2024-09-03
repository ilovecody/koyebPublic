package ray.lee.myRestApi.controller.utilities;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import ray.lee.myRestApi.common.exception.ForbiddenException;
import ray.lee.myRestApi.common.exception.MyRestApiException;
import ray.lee.myRestApi.common.exception.ResourceNotFoundException;
import ray.lee.myRestApi.common.exception.TooManyRequestsException;
import ray.lee.myRestApi.common.pojo.response.ApiFailedResponse;
import ray.lee.myRestApi.common.utilities.MessageCode;
import ray.lee.myRestApi.common.utilities.MyRestConstants;
import ray.lee.myRestApi.common.utilities.i18NUtil;

@Hidden
@Slf4j
@ControllerAdvice
public class ControllerExceptionAdvice {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, ConstraintViolationException.class})
	@ResponseBody
	public ApiFailedResponse validExceptionHandler(HttpServletRequest request, Exception e) {
		StringBuilder sb = new StringBuilder();
		if(e instanceof MethodArgumentNotValidException) {
			((MethodArgumentNotValidException)e).getBindingResult().getAllErrors()
			.forEach(error -> sb.append(i18NUtil.getMessage(((FieldError)error).getDefaultMessage())).append(","));
		} else if(e instanceof BindException) {
			((BindException)e).getBindingResult().getAllErrors()
			.forEach(error -> sb.append(i18NUtil.getMessage(((FieldError)error).getDefaultMessage())).append(","));
		} else if(e instanceof ConstraintViolationException) {
			((ConstraintViolationException)e).getConstraintViolations()
			.forEach(error -> sb.append(i18NUtil.getMessage(error.getMessage())).append(","));
		}
		return this.buildExceptionResponse(request, MessageCode.Param_Validation_Error, sb.substring(0, sb.length()-1));
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MyRestApiException.class)
	@ResponseBody
	public ApiFailedResponse myRestApiExceptionHandler(HttpServletRequest request, MyRestApiException e) {
		return this.buildExceptionResponse(request, e.getMessageCode(), e.getMessage());
	}
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseBody
	public ApiFailedResponse resourceNotFoundExceptionHandler(HttpServletRequest request, ResourceNotFoundException e) {
		return this.buildExceptionResponse(request, e.getMessageCode(), e.getMessage());
	}
	
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(ForbiddenException.class)
	@ResponseBody
	public ApiFailedResponse forbiddenExceptionHandler(HttpServletRequest request, ForbiddenException e) {
		return this.buildExceptionResponse(request, e.getMessageCode(), e.getMessage());
	}
	
	@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
	@ExceptionHandler(TooManyRequestsException.class)
	@ResponseBody
	public ApiFailedResponse tooManyRequestsExceptionHandler(HttpServletRequest request, TooManyRequestsException e) {
		return this.buildExceptionResponse(request, e.getMessageCode(), e.getMessage());
	}	
	
	private ApiFailedResponse buildExceptionResponse(HttpServletRequest request, String errorCode, String errorMsg) {
		ApiFailedResponse res = ApiFailedResponse.buildFailedResponse(errorCode, 
																	  errorMsg, 
																	  request.getServletPath(), 
																	  HttpMethod.valueOf(request.getMethod()));
		log.info("[{}] ApiFailedResponse = {}", request.getSession().getId(), res.toJsonString());
		try {
			Object requestBodyCache = request.getAttribute(MyRestConstants.REQUEST_ATTR_RequestBody_Cache);
			if(requestBodyCache != null) {
				log.debug("param = {}", StringUtils.trimAllWhitespace(requestBodyCache.toString()));
			}
		} catch(Exception e) {
			
		}
		return res;
	}
}
