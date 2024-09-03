package ray.lee.myRestApi.base.components;

import lombok.Data;
import ray.lee.myRestApi.common.utilities.MessageCode;
import ray.lee.myRestApi.common.utilities.i18NUtil;

@Data
public abstract class BaseException extends Exception {
	private String messageCode = MessageCode.Exception;
	private String message;
	
	public BaseException(Exception e) {
		super(e);
		this.message = e.getMessage();
	}
	
	public BaseException(String messageCode) {
		super(i18NUtil.getMessage(messageCode));
		this.messageCode = messageCode;
		this.message = i18NUtil.getMessage(messageCode);
	}
	
	public BaseException(String messageCode, String message) {
		super(message);
		this.messageCode = messageCode;
		this.message = message;
	}
}
