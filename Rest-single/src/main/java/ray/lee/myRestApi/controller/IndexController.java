package ray.lee.myRestApi.controller;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.linecorp.bot.spring.boot.support.LineMessageHandlerSupport;

import io.jsonwebtoken.lang.Arrays;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import ray.lee.myRestApi.base.components.BaseController;
import ray.lee.myRestApi.base.components.ServerContext;
import ray.lee.myRestApi.common.pojo.request.OauthRequest;
import ray.lee.myRestApi.common.utilities.MyRestConstants.ActionId;
import ray.lee.myRestApi.common.utilities.MyRestConstants.ServiceId;

@Slf4j
@Controller
public class IndexController extends BaseController {
	@Resource
	private ObjectMapper om;
	
	@RequestMapping("/")
	public ModelAndView indexPage(HttpServletRequest request) {
		log.debug("request ip = {}", super.getRequestIp(request));
		return new ModelAndView("index");
	}

	@RequestMapping("/db-info")
	public ModelAndView h2dbInfo(HttpServletRequest request) {
		log.debug("request ip = {}", super.getRequestIp(request));
		return new ModelAndView("pages/Dbinfo");
	}
	
	@RequestMapping("/koyebHealthCheck")
	@ResponseBody
    public String healthCheck() {
		return "ok";
	}
	
	@RequestMapping("/linetest/{msg}")
	@ResponseBody
    public String linetest(@PathVariable("msg") String msg) {
		ServerContext ctx = new ServerContext();
		ctx.setServiceId(ServiceId.lineBotService);
		ctx.setActionId(ActionId.LineBot_PushMessage);
		ctx.setRequestParameter(msg);
		try {
			this.doService(ctx);
		} catch (Exception e) {
			log.debug("indexController.linetest() failed : " + e, e);
		}
		return "ok!";
	}
}
