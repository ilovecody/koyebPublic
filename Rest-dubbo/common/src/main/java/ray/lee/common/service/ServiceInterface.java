package ray.lee.common.service;

public interface ServiceInterface {
	static String ACTION_SERVICE_ID = "Action_Service_Id";
	static String ACTION_REQUEST_ID = "Action_Request_Id";
	static String ACTION_PARM = "Action_Parameter";
	static String ACTION_RESPONSE = "Action_Response";
	static String ACTION_SESSION_ID = "Action_Session_Id";
	
	public ServerContext doService(ServerContext ctx) throws Exception;
}