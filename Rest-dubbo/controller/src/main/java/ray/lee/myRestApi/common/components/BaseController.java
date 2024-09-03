package ray.lee.myRestApi.common.components;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import ray.lee.common.constants.MessageCode;
import ray.lee.common.exceptions.BaseException;
import ray.lee.common.exceptions.MyRestApiException;
import ray.lee.common.pojo.vo.UserCredential;
import ray.lee.common.service.ServerContext;
import ray.lee.common.service.ServiceInterface;
import ray.lee.myRestApi.controller.utilities.HttpRequestHelper;
import ray.lee.myRestApi.controller.utilities.SessionHelper;

@Slf4j
public abstract class BaseController {
	@Resource
	private HttpServletRequest request;
	@Resource
	private HttpRequestHelper httpRequestHelper;
	@Resource
	private ApplicationContext applicationContext;
	@Resource
	private ServiceInterface dubboService;

	protected ServerContext doService(ServerContext ctx) throws Exception {
		if(ctx == null) {
			throw new MyRestApiException(MessageCode.Exception, "BaseControllerBean.doService() failed -- ServerContext is null");
		} else if(ctx.getServiceId() == null) {
			throw new MyRestApiException(MessageCode.Exception, "BaseControllerBean.doService() failed -- ServiceId is null");	
		} else if (ctx.getActionId() == null) {
			throw new MyRestApiException(MessageCode.Exception, "BaseControllerBean.doService() failed -- actionId is null");
		}
		
		log.info("BaseControllerBean.doService():serviceId="+ctx.getServiceId().toString()+", antionId="+ctx.getActionId().toString());
		ServiceInterface service = null;
		ServerContext rsCtx = null;
		try {
			/*
			String serviceId = ctx.getServiceId().toString();
			service = applicationContext.getBean(serviceId, ServiceInterface.class);
			if(service == null) {
				throw new MyRestApiException(MessageCode.Exception, "Service not found: "+serviceId);
			}
			rsCtx = service.doService(ctx);
			*/
			
			//Bubbo Service
			ctx.setSessionId(SessionHelper.getSessionId(request));
			rsCtx = dubboService.doService(ctx);
		} catch(BaseException be) {
			//Bubbo Service拋出自定義exception時,message為null值,需透過controller處理i18N文字後set message
			be.processI18NMessage();
			log.debug("BaseControllerBean.doService() failed: {}", be.getMessage());
			throw be;
		} catch(Exception e) {
			log.debug("BaseControllerBean.doService() failed: " + e, e);
			throw new MyRestApiException(e);
		}
		return rsCtx;			
	}

	public String getString(HttpServletRequest request, String parameterName) {
		return httpRequestHelper.getString(request, parameterName);
	}
	
	public int getInt(HttpServletRequest request, String parameterName) {
		return httpRequestHelper.getInt(request, parameterName);
	}
	
	public Integer getIntegerObject(HttpServletRequest request, String parameterName) {
		return httpRequestHelper.getIntegerObject(request, parameterName);
	}	
	
	public boolean getBoolean(HttpServletRequest request, String parameterName) {
		return httpRequestHelper.getBoolean(request, parameterName);
	}
	
	public Boolean getBooleanObject(HttpServletRequest request, String parameterName) {
		return httpRequestHelper.getBooleanObject(request, parameterName);
	}
	
	public double getDouble(HttpServletRequest request,String parameterName) {
		return httpRequestHelper.getDouble(request, parameterName);
	}	
	
	public Double getDoubleObject(HttpServletRequest request,String parameterName) {
		  return httpRequestHelper.getDoubleObject(request, parameterName);
	}
	
	public float getFloat(HttpServletRequest request,String parameterName) {
		return httpRequestHelper.getFloat(request, parameterName);
	}
	
	public Float getFloatObject(HttpServletRequest request,String parameterName) {
		return httpRequestHelper.getFloatObject(request, parameterName);
	}	
	
	public long getLong(HttpServletRequest request,String parameterName) {
		return httpRequestHelper.getLong(request, parameterName);
	}
	
	public Long getLongObject(HttpServletRequest request,String parameterName) {
		return httpRequestHelper.getLongObject(request, parameterName);
	}
	
	public Number getNumber(HttpServletRequest request,String parameterName, Class numberClass) {
		return httpRequestHelper.getNumber(request, parameterName, numberClass);
	}	
	
	public short getShort(HttpServletRequest request,String parameterName) {
		return httpRequestHelper.getShort(request, parameterName);
	}
	
	public Short getShortObject(HttpServletRequest request,String parameterName) {
		return httpRequestHelper.getShortObject(request, parameterName);
	}	
	
	public Date getDate(HttpServletRequest request,String parameterName) {
		return httpRequestHelper.getDate(request, parameterName);
	}
	
	public File getFile(HttpServletRequest request,String parameterName) throws Exception{
		return httpRequestHelper.getFile(request, parameterName);
	}
	
	public InputStream getInputStream(HttpServletRequest request,String parameterName) throws Exception {
		return httpRequestHelper.getInputStream(request, parameterName);
	}
	
	public void setAttribute(HttpServletRequest request, String attributeName, Object objAttribute)throws Exception{
		SessionHelper.setAttribute(request, attributeName, objAttribute);
	}

	public void setAttribute(HttpServletRequest request, String useCaseId, String attributeName, Object objAttribute)throws Exception{
		SessionHelper.setAttribute(request, useCaseId, attributeName, objAttribute);
	}
	
	public void removeAttribute(HttpServletRequest request, String useCaseId) {
		SessionHelper.removeAttribute(request, useCaseId);
	}
	
	public void removeAttribute(HttpServletRequest request, String useCaseId, String attributeName) {
		SessionHelper.removeAttribute(request, useCaseId, attributeName);
	}
	
	public Map getAttributes(HttpServletRequest request, String useCaseId) throws Exception {
		return SessionHelper.getAllAttribute(request, useCaseId);
	 }
	
	public Object getAttribute(HttpServletRequest request, String useCaseId, String attributeName) throws Exception {
		return SessionHelper.getAttribute(request, useCaseId, attributeName);
	 }	
	
	public void clearSession(HttpServletRequest request) {
		SessionHelper.clearSession(request);
	 }
	
	public void clearSession(HttpServletRequest request, String ucNo) {
		SessionHelper.clearSession(request, ucNo);
	}
	
	public UserCredential getUserSession(HttpServletRequest request) {
		return SessionHelper.getUserSession(request);
	}
	
	public void setUserSession(HttpServletRequest request, UserCredential user) {
		SessionHelper.setUserSession(request, user);
	}
	
	public Locale getRequestLocale(HttpServletRequest request) {
		return httpRequestHelper.getRequestLocale(request);
	} 
	
	public void doServiceFailureResponse(String errorCode) throws MyRestApiException {
		if(!StringUtils.hasText(errorCode)) {
			throw new MyRestApiException(MessageCode.ServiceReturnError);	
		}
		throw new MyRestApiException(errorCode);
	}
	
	public UserCredential getUserCredential(HttpServletRequest request) {
		return httpRequestHelper.getUserCredential(request);
	}
	
	public String getRequestIp(HttpServletRequest request) {
		return httpRequestHelper.getRequestIp(request);
	}
}
