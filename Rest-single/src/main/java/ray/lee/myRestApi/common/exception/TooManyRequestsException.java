package ray.lee.myRestApi.common.exception;

import ray.lee.myRestApi.base.components.BaseException;

public class TooManyRequestsException extends BaseException {

	public TooManyRequestsException(String messageCode) {
		super(messageCode);
	}
	
	public TooManyRequestsException(String messageCode, String message) {
		super(messageCode, message);
	}
}
