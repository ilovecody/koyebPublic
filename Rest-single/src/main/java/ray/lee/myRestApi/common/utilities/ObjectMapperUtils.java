package ray.lee.myRestApi.common.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperUtils {
	private static final ObjectMapper om = new ObjectMapper();
	
	public static String toJsonString(Object obj) throws JsonProcessingException {
		return om.writeValueAsString(obj);
	}
	
	public static Object toObject(String json, Class clazz) throws JsonMappingException, JsonProcessingException {
		return om.readValue(json, clazz);
	}	
}
