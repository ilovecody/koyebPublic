package ray.lee.myRestApi.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.AudioMessage;
import com.linecorp.bot.model.message.FlexMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.flex.component.Box;
import com.linecorp.bot.model.message.flex.component.FlexComponent;
import com.linecorp.bot.model.message.flex.component.Span;
import com.linecorp.bot.model.message.flex.component.Text;
import com.linecorp.bot.model.message.flex.component.Text.TextWeight;
import com.linecorp.bot.model.message.flex.container.Bubble;
import com.linecorp.bot.model.message.flex.container.Carousel;
import com.linecorp.bot.model.message.flex.unit.FlexLayout;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ray.lee.myRestApi.base.components.ServerContext;
import ray.lee.myRestApi.base.components.ServiceInterface;
import ray.lee.myRestApi.common.pojo.GoogleAppScriptResoultVO;
import ray.lee.myRestApi.common.pojo.TongueTwisterAudioVO;

@Slf4j
@Service
public class LineBotService implements ServiceInterface {
	private static final List<TongueTwisterAudioVO> ttAudioMsg = new ArrayList();
	
	@Resource
	private LineMessagingClient lineClient;
	@Resource(name="gasRestTemplate")
	private RestTemplate gasRestTemplate;
	@Resource
	private ObjectMapper om;
	
	@PostConstruct
	public void init() {
	}
	
	@Override
	public ServerContext doService(ServerContext ctx) throws Exception {
		switch(ctx.getActionId()) {
			case LineBot_MessageEvent:
				return this.handleMessageEvent(ctx);
			case LineBot_PushMessage :
				//return this.pushMessageTest(ctx);
		}
		return ctx;
	}
	
	private ServerContext handleMessageEvent(ServerContext ctx) {
		try {
			MessageEvent<TextMessageContent> event= (MessageEvent)ctx.getRequestParameter();
			List<Message> replyMsg = new ArrayList();
			
			Map<String, String> lineCommand = this.builedLineCommand(event.getMessage().getText());						
			if(!lineCommand.isEmpty() && lineCommand.containsKey("繞口令")) {
				replyMsg = this.findTongueTwister();
			} else if(!lineCommand.isEmpty() && lineCommand.containsKey("Check")) {
				replyMsg.add(new TextMessage("Server時間:\n" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())));
			}
			
			if(replyMsg.isEmpty()) {
				replyMsg.add(new TextMessage("沒有這個指令喔"));	
			}
			this.lineReplyMessage(event.getReplyToken(), replyMsg);	
		} catch(Exception e) {
			log.info(e.getMessage(), e);
		}
		return ctx;
	}
	
	private List<Message> findTongueTwister() throws URISyntaxException {
		int index = this.getRandomInt(ttAudioMsg.size());
		TongueTwisterAudioVO vo = ttAudioMsg.get(index);
		TextMessage textMsg = new TextMessage(vo.getTextMsg());
		AudioMessage audioMsg = new AudioMessage(new URI(vo.getUrl()), vo.getDuration());
		
		List<Message> reply = new ArrayList();
		reply.add(textMsg);
		reply.add(audioMsg);
		return reply;
	}

	private int getRandomInt(int scope) {
		return new Random().nextInt(scope);
	}
	
	private void lineReplyMessage(String replyToken, List<Message> replyMsg) {
		lineClient.replyMessage(new ReplyMessage(replyToken, replyMsg));
	}
	
	public void linePushMessage(String to, List<Message> pushMsg) {
		lineClient.pushMessage(new PushMessage(to, pushMsg));
	}	
	
	private Map<String, String> builedLineCommand(String msg) {
		Map<String, String> command = new HashMap();
		if(msg.indexOf("繞口令") != -1) {
			command.put("繞口令", msg);
		} else if(msg.indexOf("Check") != -1) {
			command.put("Check", msg);
		} else if(msg.startsWith("#")) {
			msg = msg.substring(1);
			try {
				command.put(msg.substring(0, msg.indexOf("#")), msg.substring(msg.indexOf("#")+1));
			} catch(Exception e) {
				log.debug("LineBotService.builedLineCommand failed... msg = " + msg);
			}
		}
		return command;
	}
	
}
