package ray.lee.common.logger;

import java.text.MessageFormat;
import java.util.Arrays;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AopLogger {
	@Resource
	private ObjectMapper om;
	
	public void logger(Logger log, String message, String sessionId, Object[] args) {
    	message = MessageFormat.format("[{0}] ", sessionId)+message;
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
