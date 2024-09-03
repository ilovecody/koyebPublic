package ray.lee.myRestApi.controller.linebot;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import ray.lee.myRestApi.base.components.BaseController;
import ray.lee.myRestApi.base.components.ServerContext;
import ray.lee.myRestApi.common.utilities.MyRestConstants.ActionId;
import ray.lee.myRestApi.common.utilities.MyRestConstants.ServiceId;

@Slf4j
@Controller
public class LineBotController extends BaseController {
	private static final ObjectMapper myMapper = new ObjectMapper();
	
	@PostConstruct
	public void init() {
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(Instant.class, new MyInstantSerializer());
		myMapper.registerModule(javaTimeModule);
		myMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}
	
    private class MyInstantSerializer extends InstantSerializer {
    	public MyInstantSerializer() {
    		super(InstantSerializer.INSTANCE, 
    			  false, 
    			  false, 
    			  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()));
    	}
    }
    
    public void excuteDefaultMessageEvent(Event event) {
        this.logEvent(event);
    }
    
    public void excuteMessageEvent(MessageEvent<TextMessageContent> event) {
    	this.logEvent(event);
    	try {
    		ServerContext ctx = new ServerContext();
    		ctx.setServiceId(ServiceId.lineBotService);
    		ctx.setActionId(ActionId.LineBot_MessageEvent);
    		ctx.setRequestParameter(event);
    		this.doService(ctx);
    	} catch(Exception e) {
    		log.info(e.getMessage(), e);
    	}
    }
    
    private void logEvent(Event event) {
        try {
			log.info(myMapper.writeValueAsString(event));
		} catch (JsonProcessingException e) {
			log.info(e.getMessage(), e);
		}
    }
}
