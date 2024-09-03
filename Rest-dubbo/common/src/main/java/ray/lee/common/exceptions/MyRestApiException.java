package ray.lee.common.exceptions;

public class MyRestApiException extends BaseException {
	public MyRestApiException(Exception e) {
		super(e);
	}
	
	public MyRestApiException(String messageCode) {
		super(messageCode);
	}
	
	public MyRestApiException(String messageCode, String message) {
		super(messageCode, message);
	}
}
