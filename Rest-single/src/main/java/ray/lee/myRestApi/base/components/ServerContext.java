package ray.lee.myRestApi.base.components;

import java.io.Serializable;
import java.util.HashMap;

import ray.lee.myRestApi.common.pojo.UserCredential;
import ray.lee.myRestApi.common.utilities.MyRestConstants.ActionId;
import ray.lee.myRestApi.common.utilities.MyRestConstants.ServiceId;

public class ServerContext implements Serializable {
	private static final long serialVersionUID = 1L;
	private static String ACTION_SERVICE_ID = "Action_Service_Id";
	private static String ACTION_REQUEST_ID = "Action_Request_Id";
	private static String ACTION_PARM 		= "Action_Parameter";
	private static String ACTION_RESPONSE 	= "Action_Response";	
	
	private HashMap attributes = new HashMap();
	private ServerMessage returnMessage;
	private UserCredential user;
	
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
	
	public ServerMessage getReturnMessage() {
		return returnMessage;
	}
	
	public void setReturnMessage(ServerMessage returnMessage) {
		this.returnMessage = returnMessage;
	}	
	
	public UserCredential getUser() {
		return user;
	}

	public void setUser(UserCredential user) {
		this.user = user;
	}		
	
	public ServiceId getServiceId() {
		return (ServiceId)this.getAttribute(ACTION_SERVICE_ID);
	}
	
	public void setServiceId(ServiceId serviceId) {
		this.setAttribute(ACTION_SERVICE_ID, serviceId);
	}	
	
	public ActionId getActionId() {
		return (ActionId)this.getAttribute(ACTION_REQUEST_ID);
	}
	
	public void setActionId(ActionId actionId) {
		setAttribute(ACTION_REQUEST_ID, actionId);
	}
	
	public Object getRequestParameter() {
		return this.getAttribute(ACTION_PARM);
	}

	public <T> T getRequestParameter(Class<T> clazz) {
		return (T)this.getRequestParameter();
	}
	public void setRequestParameter(Object parameter) {
		setAttribute(ACTION_PARM, parameter);
	}
	
	public Object getResponseResult() {
		return this.getAttribute(ACTION_RESPONSE);
	}
	
	public <T> T getResponseResult(Class<T> clazz) {
		return (T)this.getResponseResult();
	}
	
	public void setResponseResult(Object result) {
		setAttribute(ACTION_RESPONSE, result);
	}
}
