package ray.lee.myRestApi.controller.utilities;

import java.io.IOException;
import java.security.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.annotation.Resource;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import ray.lee.common.constants.MessageCode;
import ray.lee.common.pojo.response.ApiFailedResponse;
import ray.lee.myRestApi.common.components.MyHttpServletRequestWrapper;

@Slf4j
@Controller
public class MyErrorController implements ErrorController {
	@Resource
    private ErrorAttributes errorAttributes;
	
	@RequestMapping("/error")
	@ResponseBody
	public ApiFailedResponse errorHandler(WebRequest request) {
		Map<String, Object> errorAttribute = errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());
		HttpMethod httpMethod = ((ServletWebRequest)request).getHttpMethod();
		HttpStatus status = HttpStatus.valueOf(Integer.parseInt(errorAttribute.get("status").toString()));
		
		String exMsg = status.name();
		Object exObj = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION, WebRequest.SCOPE_REQUEST);		
		if(exObj != null) {
			exMsg = exObj.toString();
		}
		
		ApiFailedResponse res = ApiFailedResponse.buildFailedResponse(String.valueOf(status.value()),
																	  status.name(),
																	  errorAttribute.get("path").toString(),
																	  httpMethod);
		if(HttpStatus.BAD_REQUEST.equals(status)) {
			res = ApiFailedResponse.buildFailedResponse(MessageCode.Param_RequestBody_Error, 
														errorAttribute.get("path").toString(),
														httpMethod);			
			exMsg += ","+res.getErrorMessage();
			try {
				Object requestBodyCache = request.getAttribute(MyRestConstants.REQUEST_ATTR_RequestBody_Cache, WebRequest.SCOPE_REQUEST);		
				if(requestBodyCache != null) {
					exMsg += " param="+StringUtils.trimAllWhitespace(requestBodyCache.toString());	
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		log.info("Global Error - {}", errorAttribute);
		log.info("Global Error - exception={}", exMsg);
		return res;
	}

	public String getErrorPath() {
		return "/error";
	}	
}
