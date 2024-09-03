package ray.lee.myRestApi.common.filters;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import ray.lee.myRestApi.common.others.MyHttpServletRequestWrapper;
import ray.lee.myRestApi.common.pojo.JwtToken;
import ray.lee.myRestApi.common.pojo.UserCredential;
import ray.lee.myRestApi.common.pojo.response.ApiFailedResponse;
import ray.lee.myRestApi.common.utilities.CryptoUtils;
import ray.lee.myRestApi.common.utilities.JwtUtils;
import ray.lee.myRestApi.common.utilities.MessageCode;
import ray.lee.myRestApi.common.utilities.MyRestConstants;
import ray.lee.myRestApi.controller.rest.JwtLoginController;
import ray.lee.myRestApi.dao.JwtUserDao;
import ray.lee.myRestApi.model.JwtUser;
import ray.lee.myRestApi.services.JwtService;

@WebFilter(urlPatterns = "/order/*")
@Order(1)
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	@Resource
	private JwtUserDao userDao;
	
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
        JwtToken jwtToken = new JwtToken(CryptoUtils.decrypt(token));
        if(!StringUtils.hasText(jwtToken.getClientId())) {
        	this.returnFailedResponse(response, MessageCode.JwtToken_invalid, 
        							  request.getServletPath(), HttpMethod.valueOf(request.getMethod()));
        	return false;
        }
        //find by DB or redis or others
        JwtUser user = userDao.findByClientId(jwtToken.getClientId());
        if(user == null) {
        	log.info("user is null");
        	this.returnFailedResponse(response, MessageCode.JwtToken_invalid, 
        							  request.getServletPath(), HttpMethod.valueOf(request.getMethod()));
        	return false;
        }
        
        if(!JwtUtils.isTokenValid(jwtToken.getToken(), user.getClientId(), user.getClientSecret())) {
        	log.info("jwt validation failed.");
        	this.returnFailedResponse(response, MessageCode.JwtToken_invalid, 
        							  request.getServletPath(), HttpMethod.valueOf(request.getMethod()));
        	return false;
        }
        if(JwtUtils.isTokenExpired(jwtToken.getToken(), user.getClientSecret())) {
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
}
