package ray.lee.myRestApi.common.exception;

import ray.lee.myRestApi.base.components.BaseException;

public class ResourceNotFoundException extends BaseException {
	
	public ResourceNotFoundException(String messageCode) {
		super(messageCode);
	}

	public ResourceNotFoundException(String messageCode, String message) {
		super(messageCode, message);
	}
}
