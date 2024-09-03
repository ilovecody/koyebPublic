package ray.lee.common.exceptions;

public class ForbiddenException extends BaseException {
	
	public ForbiddenException(String messageCode) {
		super(messageCode);
	}

	public ForbiddenException(String messageCode, String message) {
		super(messageCode, message);
	}
}
