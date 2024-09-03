package ray.lee.myRestApi.controller.utilities;

/**
 * 定義一般常數
 */
public interface MyRestConstants {
	public static final String SystemDefaultTimezone 			= "Asia/Taipei";
	public static final String oauthPath 						= "/oauth/token";
	//common value alias	
	public static final String TRUE               				= "T";
	public static final String FALSE              				= "F";
	public static final String YES								= "Y";
	public static final String NO								= "N";
	public static final String MALE								= "M";
	public static final String FEMALE							= "F";
	public static final String SUCCESS							= "SUCCESS";
	public static final String FAILED							= "FAILED";
	
	//Filter order
	public static final int Web_Filter_Order_1 					= 1;
	public static final int Web_Filter_Order_2 					= 2;

	//Session Attribute定義
	public final static String SESS_ATTR_COMMON					= "common";
	public final static String SESS_ATTR_USER_SESSION			= "UserSession";
	
	//Request Attribute定義
	public final static String REQUEST_ATTR_RequestBody_Cache	= "MyRquestBodyCache";
	public final static String REQUEST_ATTR_USER_Credential		= "MyRestUserCredential";
}