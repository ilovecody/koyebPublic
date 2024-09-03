package ray.lee.myRestApi.common.exception;

import ray.lee.myRestApi.base.components.BaseException;

public class ForbiddenException extends BaseException {
	
	public ForbiddenException(String messageCode) {
		super(messageCode);
	}

	public ForbiddenException(String messageCode, String message) {
		super(messageCode, message);
	}
}
