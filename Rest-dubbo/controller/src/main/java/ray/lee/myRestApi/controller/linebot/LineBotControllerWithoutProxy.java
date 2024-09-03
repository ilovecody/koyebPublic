package ray.lee.myRestApi.controller.linebot;

import org.springframework.stereotype.Controller;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import jakarta.annotation.Resource;

/**
 * 不能繼承BaseController,因為Spring AOP LogControllerAspect的關係,
 * 若繼承的話,則會被cglib動態產生的proxy影響,產生出來的代理為子類別,
 * 因此@EventMapping無法被LineMessageHandlerSupport.refresh()處理,
 * 會造成無法監聽Linebot的webhook event,所以先用一隻單純的withoutProxy controller專門監聽linebot的webhook event,
 * 接收到Event後,再實際調用LineBotController來處理doService()相關的流程.
 */
@Controller
@LineMessageHandler
public class LineBotControllerWithoutProxy {
	@Resource
	private LineBotController lineBotController;
	
    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
    	lineBotController.excuteDefaultMessageEvent(event);
    }
    
    @EventMapping
    public void handleMessageEvent(MessageEvent<TextMessageContent> event) {
    	lineBotController.excuteMessageEvent(event);
    }
}
