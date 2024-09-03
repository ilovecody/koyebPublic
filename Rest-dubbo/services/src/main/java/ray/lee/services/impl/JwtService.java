package ray.lee.services.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import ray.lee.common.constants.MessageCode;
import ray.lee.common.pojo.request.OauthRequest;
import ray.lee.common.pojo.response.ApiTokenResponse;
import ray.lee.common.pojo.vo.RestToken;
import ray.lee.common.service.ServerContext;
import ray.lee.common.service.ServerMessage;
import ray.lee.common.service.ServiceInterface;
import ray.lee.model.JwtUser;
import ray.lee.model.Enum.UserRole;
import ray.lee.services.dao.JwtUserDao;
import ray.lee.utilities.CryptoUtils;
import ray.lee.utilities.JwtUtils;

@Service
public class JwtService implements ServiceInterface {
	@Resource
	private JwtUserDao jwtUserDao;
	@Resource
	private ObjectMapper om;

	@Override
	public ServerContext doService(ServerContext ctx) throws Exception {
		switch(ctx.getActionId()) {
			case jwt_getToken:
				return this.getToken(ctx);
			case jwt_getUserByClientId:
				return this.getUserByClientId(ctx);
		}
		return null;
	}

	public ServerContext getToken(ServerContext ctx) throws Exception {
		OauthRequest loginInfo = ctx.getRequestParameter(OauthRequest.class);
		JwtUser user = jwtUserDao.findByClientId(loginInfo.getClientId());
		
		if(user == null || !user.getClientSecret().equals(loginInfo.getClientSecret())) {
			ctx.setReturnMessage(new ServerMessage(ServerMessage.STATUS_FAILURE, MessageCode.JwtToken_User_DoesNotExist)); 
		} else {
			ApiTokenResponse res = new ApiTokenResponse();
			String jwtToken = JwtUtils.generateToken(user.getClientId(), user.getClientSecret());
			RestToken restToken = new RestToken(System.currentTimeMillis(), user.getClientId(), jwtToken);
			res.setToken(CryptoUtils.encrypt(om.writeValueAsString(restToken)));
			res.setExpiresIn(TimeUnit.SECONDS.convert(JwtUtils.EXPIRATION_TIME, TimeUnit.MILLISECONDS));
			ctx.setReturnMessage(new ServerMessage());
			ctx.setResponseResult(res);
		}
		return ctx;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public JwtUser createJwtUser() throws Exception {
		String clientId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(0, 16);
		while(jwtUserDao.findByClientId(clientId) != null) {
			clientId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(0, 16);
		}
		
		JwtUser user = new JwtUser();
		user.setClientId(clientId);
		user.setClientSecret(Base64Utils.encodeToUrlSafeString(MessageDigest.getInstance("SHA-256").digest(clientId.getBytes()))
					   		.toLowerCase().replaceAll("=", ""));
		user.setName("Ray test");
		user.setRole(UserRole.Owner);
		return jwtUserDao.createJwtUser(user);
	}
	
	public ServerContext getUserByClientId(ServerContext ctx) throws Exception {
		String clientId = ctx.getRequestParameter(String.class);
		JwtUser user = jwtUserDao.findByClientId(clientId);
		if(user == null) {
			ctx.setReturnMessage(new ServerMessage(ServerMessage.STATUS_FAILURE));
		} else {
			ctx.setReturnMessage(new ServerMessage());
			ctx.setResponseResult(user);
		}
		return ctx;
	}
}
