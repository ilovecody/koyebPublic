package ray.lee.myRestApi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import ray.lee.common.service.ServerContext;
import ray.lee.common.service.Enum.ActionId;
import ray.lee.common.service.Enum.ServiceId;
import ray.lee.myRestApi.common.components.BaseController;

@Slf4j
@Controller
public class indexController extends BaseController {
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
    public String healthCheck() throws JsonProcessingException {
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
