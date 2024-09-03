package ray.lee.common.service;

import java.io.Serializable;
import java.util.HashMap;

import ray.lee.common.pojo.vo.UserCredential;
import ray.lee.common.service.Enum.ActionId;
import ray.lee.common.service.Enum.ServiceId;

public class ServerContext implements Serializable {
	private static final long serialVersionUID = 1L;
	UserCredential user;
	HashMap attributes = new HashMap();
	ServerMessage returnMessage;
		
	public HashMap getAttributes() {
		return attributes;
	}
	
	public void setAttributes(HashMap attributes) {
		this.attributes = attributes;
	}
	public void setAttribute(String key, Object value) {
		this.attributes.put(key, value);
	}
	public Object getAttribute(String key) {
		return this.attributes.get(key);
	}
	
	public UserCredential getUser() {
		return user;
	}

	public void setUser(UserCredential user) {
		this.user = user;
	}		
	
	public ServerMessage getReturnMessage() {
		return returnMessage;
	}
	
	public void setReturnMessage(ServerMessage returnMessage) {
		this.returnMessage = returnMessage;
	}
	
	public ServiceId getServiceId() {
		return (ServiceId)this.getAttribute(ServiceInterface.ACTION_SERVICE_ID);
	}
	
	public void setServiceId(ServiceId serviceId) {
		setAttribute(ServiceInterface.ACTION_SERVICE_ID, serviceId);
	}	
	
	public ActionId getActionId() {
			return (ActionId)this.getAttribute(ServiceInterface.ACTION_REQUEST_ID);
	}
	
	public void setActionId(ActionId actionId) {
			setAttribute(ServiceInterface.ACTION_REQUEST_ID, actionId);
	}
	
	public Object getRequestParameter() {
		return this.getAttribute(ServiceInterface.ACTION_PARM);
	}

	public <T> T getRequestParameter(Class<T> clazz) {
			return (T) this.getRequestParameter();
	}
	public void setRequestParameter(Object parameter) {
			setAttribute(ServiceInterface.ACTION_PARM, parameter);
	}
	
	public Object getResponseResult(){
			return this.getAttribute(ServiceInterface.ACTION_RESPONSE);
	}
	
	public <T> T getResponseResult(Class<T> clazz) {
		return (T) this.getResponseResult();
	}
	
	public void setResponseResult(Object result) {
			setAttribute(ServiceInterface.ACTION_RESPONSE, result);
	}
	
	public void setSessionId(String sessionId) {
		setAttribute(ServiceInterface.ACTION_SESSION_ID, sessionId);
	}
	
	public String getSessionId() {
		return (String)this.getAttribute(ServiceInterface.ACTION_SESSION_ID);
	}
}
