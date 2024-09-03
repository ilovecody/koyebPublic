package ray.lee.myRestApi.common.aop;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import ray.lee.myRestApi.common.aop.annotation.RestapiLimitationCheck;
import ray.lee.myRestApi.common.exception.TooManyRequestsException;
import ray.lee.myRestApi.common.pojo.RestapiLimitationVO;
import ray.lee.myRestApi.common.utilities.MessageCode;
import ray.lee.myRestApi.controller.utilities.HttpRequestHelper;

@Slf4j
@Component
@Aspect
public class RestapiLimitationCheckAspect {
	private static final ConcurrentHashMap<String, RestapiLimitationVO> limitMap = new ConcurrentHashMap();
	private static final int rateLimit = 200;
	private static final long rateLimitDuration = TimeUnit.HOURS.toMillis(24);
	//private static final long rateLimitDuration = TimeUnit.MINUTES.toMillis(1);
	@Resource
	private HttpServletRequest request;
	@Resource
	private HttpServletResponse response;
	@Resource
	private HttpRequestHelper requestHelper;
	@Resource
	private ObjectMapper om;
	
	@Pointcut(value = "@within(ray.lee.myRestApi.common.aop.annotation.RestapiLimitationCheck)")
	public void classPointcut() {
		
	}
	
	@Pointcut(value = "@annotation(ray.lee.myRestApi.common.aop.annotation.RestapiLimitationCheck)")
	public void methodPointcut() {
		
	}
	
	@Around(value = "classPointcut() || methodPointcut()")
	public Object beforeController(ProceedingJoinPoint joinPoint) throws Throwable {
		RestapiLimitationCheck restApiCheck = this.getAnnotation(joinPoint);
		if(restApiCheck.showLimitaion()) {
			try {
				response.getWriter().write(om.writeValueAsString(limitMap));
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
			}
		} else {
			if(this.isRateLimited()) {
				throw new TooManyRequestsException(MessageCode.Rest_Too_Many_Requests);
			} else {
				return joinPoint.proceed();
			}
		}
		return null;
	}
	
	private RestapiLimitationCheck getAnnotation(ProceedingJoinPoint joinPoint) {
		RestapiLimitationCheck restApiCheck = ((MethodSignature)joinPoint.getSignature())
												.getMethod().getAnnotation(RestapiLimitationCheck.class);
		if(restApiCheck == null) {
			restApiCheck = joinPoint.getTarget().getClass().getAnnotation(RestapiLimitationCheck.class);
		}
		return restApiCheck;
	}
	
	private boolean isRateLimited() {
		String requestIp = requestHelper.getRequestIp(request);
		if(limitMap.get(requestIp) == null) {
			limitMap.put(requestIp, this.addApiCount(new RestapiLimitationVO()));
			return false;
		} else if(limitMap.get(requestIp).getCounter().get() >= rateLimit) {
			//若次數已達上限,則判斷是否已超過24小時,若超過則重設計數器
			if(System.currentTimeMillis() - limitMap.get(requestIp).getLatestApiTime().getTime() > rateLimitDuration) {
				limitMap.get(requestIp).getCounter().set(0);
				this.addApiCount(limitMap.get(requestIp));
				return false;
			} else {
				return true;	
			}
		} else {
			this.addApiCount(limitMap.get(requestIp));
			return false;
		}
	}
	
	private RestapiLimitationVO addApiCount(RestapiLimitationVO vo) {
		LocalDateTime now = LocalDateTime.now();
		String nowString = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		vo.getApiRecords().add(nowString + ">" + request.getServletPath());
		vo.getCounter().incrementAndGet();
		vo.setLatestApiTime(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));
		return vo;
	}
}
