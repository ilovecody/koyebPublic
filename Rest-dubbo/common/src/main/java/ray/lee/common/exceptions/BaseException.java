package ray.lee.common.exceptions;

import org.springframework.util.StringUtils;

import lombok.Data;
import ray.lee.common.constants.MessageCode;
import ray.lee.utilities.i18NUtil;

@Data
public abstract class BaseException extends Exception {
	private String messageCode = MessageCode.Exception;
	private String message;
	
	public BaseException(Exception e) {
		super(e);
		this.message = e.getMessage();
	}
	
	public BaseException(String messageCode) {
		//Dubbo service端沒有i18N相關文字,所以傳回controller端時,再另外用i18NUtil set文字
		//super(i18NUtil.getMessage(messageCode));
		super("MessageCode:"+messageCode);
		this.messageCode = messageCode;
		//this.message = i18NUtil.getMessage(messageCode);
	}
	
	public BaseException(String messageCode, String message) {
		super(message);
		this.messageCode = messageCode;
		this.message = message;
	}
	
	public void processI18NMessage() {
		this.message = i18NUtil.getMessage(messageCode);
	}
	
    @Override
    public String getMessage() {
    	if(StringUtils.hasText(message)) {
    		return message;
    	} else {
    		return super.getMessage();
    	}
    }
}
