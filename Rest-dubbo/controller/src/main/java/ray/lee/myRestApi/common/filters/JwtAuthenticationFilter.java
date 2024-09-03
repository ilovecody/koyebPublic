package ray.lee.myRestApi.common.filters;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.Method;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import ray.lee.common.constants.MessageCode;
import ray.lee.common.pojo.response.ApiFailedResponse;
import ray.lee.common.pojo.vo.RestToken;
import ray.lee.common.pojo.vo.UserCredential;
import ray.lee.common.service.ServerContext;
import ray.lee.common.service.ServiceInterface;
import ray.lee.common.service.Enum.ActionId;
import ray.lee.common.service.Enum.ServiceId;
import ray.lee.model.JwtUser;
import ray.lee.myRestApi.common.components.MyHttpServletRequestWrapper;
import ray.lee.myRestApi.controller.utilities.MyRestConstants;
import ray.lee.myRestApi.controller.utilities.SessionHelper;
import ray.lee.utilities.CryptoUtils;
import ray.lee.utilities.JwtUtils;

@WebFilter(urlPatterns = "/order/*")
@Order(1)
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	@Resource
	private ServiceInterface dubboService;
	@Resource
	private ObjectMapper om;
	@Resource
	private HttpServletRequest request;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		request = this.getRequestWrapper(request);
		
        if(this.tokenVerified(request, response)) {
        	filterChain.doFilter(request, response);
        } else {
        	return;
        }
	}

	private void returnFailedResponse(HttpServletResponse response, String errorCode, String path, HttpMethod httpMethod) throws IOException {
    	response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
    	response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    	response.setStatus(HttpStatus.FORBIDDEN.value());
    	
    	PrintWriter writer = response.getWriter();
    	writer.print(ApiFailedResponse.buildFailedResponse(errorCode, path, httpMethod).toJsonString());
    	writer.flush();
    	writer.close();
	}
	
	private boolean tokenVerified(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String authHeader = request.getHeader("Authorization");
        //無Token的request
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        	this.returnFailedResponse(response, MessageCode.JwtToken_isEmpty, request.getServletPath(), HttpMethod.valueOf(request.getMethod()));
            return false;
        }
		
        String token = authHeader.substring(7);
        RestToken restToken = om.readValue(CryptoUtils.decrypt(token), RestToken.class);
        if(!StringUtils.hasText(restToken.getClientId())) {
        	this.returnFailedResponse(response, MessageCode.JwtToken_invalid, 
        							  request.getServletPath(), HttpMethod.valueOf(request.getMethod()));
        	return false;
        }
        //find by DB or redis or others
        JwtUser user = this.getUserByClientId(restToken.getClientId());
        if(user == null) {
        	log.info("user is null");
        	this.returnFailedResponse(response, MessageCode.JwtToken_invalid, 
        							  request.getServletPath(), HttpMethod.valueOf(request.getMethod()));
        	return false;
        }
        
        if(!JwtUtils.isTokenValid(restToken.getToken(), user.getClientId(), user.getClientSecret())) {
        	log.info("jwt validation failed.");
        	this.returnFailedResponse(response, MessageCode.JwtToken_invalid, 
        							  request.getServletPath(), HttpMethod.valueOf(request.getMethod()));
        	return false;
        }
        if(JwtUtils.isTokenExpired(restToken.getToken(), user.getClientSecret())) {
        	this.returnFailedResponse(response, MessageCode.JwtToken_expired, 
        							  request.getServletPath(), HttpMethod.valueOf(request.getMethod()));
        	return false;
        }
        
    	UserCredential userCredential = new UserCredential();
    	userCredential.setUserId(user.getId());
    	userCredential.setUserName(user.getName());
    	userCredential.setUserRole(user.getRole());
    	request.setAttribute(MyRestConstants.REQUEST_ATTR_USER_Credential, userCredential);      
        return true;
	}
	
	//讓request可重複讀取InputStream,可多次用在@RequestBody,可重複取得request body
	//只處理"application/json"格式
	private HttpServletRequest getRequestWrapper(HttpServletRequest request) {
		if(request.getContentType() != null && request.getContentType().equalsIgnoreCase(MimeTypeUtils.APPLICATION_JSON_VALUE)) {
			return new MyHttpServletRequestWrapper(request);	
		} else {
			return request;
		}
	}
	
	//find by DB or redis or others
	private JwtUser getUserByClientId(String clientId) {
		ServerContext ctx = new ServerContext();
		ctx.setServiceId(ServiceId.jwtService);
		ctx.setActionId(ActionId.jwt_getUserByClientId);
		ctx.setSessionId(SessionHelper.getSessionId(request));
		ctx.setRequestParameter(clientId);
		try {
			ctx = dubboService.doService(ctx);
			if(ctx.getReturnMessage().isSuccess()) {
				return ctx.getResponseResult(JwtUser.class);
			}
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
		return null;
	}
}
