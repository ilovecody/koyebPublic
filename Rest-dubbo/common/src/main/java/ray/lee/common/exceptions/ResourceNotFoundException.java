package ray.lee.common.exceptions;

public class ResourceNotFoundException extends BaseException {
	
	public ResourceNotFoundException(String messageCode) {
		super(messageCode);
	}

	public ResourceNotFoundException(String messageCode, String message) {
		super(messageCode, message);
	}
}
