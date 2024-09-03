package ray.lee.myRestApi.common.utilities;

public interface MessageCode {
	public static final String Success 						= "success";
	public static final String Exception 					= "error";
	public static final String ServiceReturnError 			= "e00001";
	public static final String Param_RequestBody_Error 		= "e00002";
	public static final String Param_Validation_Error 		= "e00003";
	public static final String Rest_Too_Many_Requests 		= "e00004";
	
	public static final String JwtToken_isEmpty 			= "t00001";
	public static final String JwtToken_invalid 			= "t00002";
	public static final String JwtToken_expired 			= "t00003";
	public static final String JwtToken_permission_denied	= "t00004";	
	public static final String JwtToken_User_DoesNotExist 	= "t00005";
	
	public static final String Order_NotFound 				= "20001";
}
