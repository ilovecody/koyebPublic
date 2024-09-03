package ray.lee.common.exceptions;

public class TooManyRequestsException extends BaseException {

	public TooManyRequestsException(String messageCode) {
		super(messageCode);
	}
	
	public TooManyRequestsException(String messageCode, String message) {
		super(messageCode, message);
	}
}
