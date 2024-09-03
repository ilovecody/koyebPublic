package ray.lee.myRestApi.common.aop;

import java.time.LocalDateTime;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import ray.lee.myRestApi.common.aop.annotation.PermissionCheck;
import ray.lee.myRestApi.common.exception.ForbiddenException;
import ray.lee.myRestApi.common.exception.MyRestApiException;
import ray.lee.myRestApi.common.pojo.UserCredential;
import ray.lee.myRestApi.common.pojo.response.ApiFailedResponse;
import ray.lee.myRestApi.common.utilities.MessageCode;
import ray.lee.myRestApi.common.utilities.MyRestConstants;
import ray.lee.myRestApi.model.Enum.UserRole;

@Slf4j
@Component
@Aspect
public class PermissionCheckAspect {
	@Resource
	private HttpServletRequest request;
	@Resource
	private HttpServletResponse response;
	
	@Around(value = "@annotation(pc)")
	public Object advice(ProceedingJoinPoint joinPoint, PermissionCheck pc) throws Throwable {
		if(isUserRoleValid(pc.role())) {
			return joinPoint.proceed();
		} else {
			//回傳status code 400
			//throw new MyRestApiException(MessageCode.JwtToken_permission_denied);
			
			//回傳status code 403
			throw new ForbiddenException(MessageCode.JwtToken_permission_denied);
		}
	}
	
	private boolean isUserRoleValid(UserRole[] checkRoles) {
		if(checkRoles.length > 0) {
			try {
				UserCredential user = (UserCredential)request.getAttribute(MyRestConstants.REQUEST_ATTR_USER_Credential);
				if(user == null) {
					log.info("[{}] UserCredential is null", request.getSession().getId());
					return false;
				}
				
				return List.of(checkRoles).stream().anyMatch(role -> role.equals(user.getUserRole()));	
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}	
}
