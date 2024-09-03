package ray.lee.myRestApi.common.aop;

import java.text.MessageFormat;
import java.util.Arrays;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AopLogger {
	@Resource
	private ObjectMapper om;
	@Resource
	private HttpServletRequest request;
	
	public void logger(Logger log, String message, Object[] args) {
    	message = MessageFormat.format("[{0}] ", request.getSession().getId())+message;
    	log.info(message);
    	if(args != null && args.length > 0) {
    		try {
        		ArrayNode jsonList = om.createArrayNode();
        		Arrays.asList(args).forEach(param -> jsonList.addPOJO(param));
        		log.debug(message+"param="+jsonList.toPrettyString());   			
    		} catch(Exception e) {
    			//log.debug(message); 
    		}
    	}
	}
}
