package ray.lee.myRestApi.controller.linebot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.activation.MimeType;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import ray.lee.myRestApi.base.components.BaseController;
import ray.lee.myRestApi.common.config.LineNotifyProperties;
import ray.lee.myRestApi.common.pojo.AjaxStatusVO;
import ray.lee.myRestApi.common.pojo.AjaxStatusVO.AjaxStatusCode;
import ray.lee.myRestApi.common.utilities.CryptoUtils;
import ray.lee.myRestApi.common.utilities.MyRestConstants;

@Slf4j
@Controller
@RequestMapping("/line/notify")
public class LineNotifyController extends BaseController {
	@Resource(name="gasRestTemplate")
	private RestTemplate gasRestTemplate;
	@Resource
	private LineNotifyProperties lineNotifyProp;
	@Resource
	private ObjectMapper om;
	
	@RequestMapping
	public ModelAndView notify(HttpServletRequest request) {
		log.debug("request ip = {}", super.getRequestIp(request));
		try {
			Object token = super.getAttribute(request, MyRestConstants.SESS_ATTR_COMMON, "lineNotifyToken");
			if(token == null) {
				super.setAttribute(request, "lineNotifyToken", null);
			}
		} catch(Exception e) {
			log.info("LineNotifyController.notify() failed : " + e, e);
		}
		return new ModelAndView("pages/LineNotify");
	}
	
	@RequestMapping("/submitUserCode")
	@ResponseBody
	public String submitUserCode(HttpServletRequest request, @RequestParam(value="userCode")String userName,
								 @RequestParam(value="recaptchaToken")String recaptchaToken) {
		AjaxStatusVO vo = new AjaxStatusVO(AjaxStatusCode.Failed);
		try {
			if(StringUtils.hasText(userName) && this.recaptchaTokenVerified(request, recaptchaToken)) {
				String token = this.findNotifyToken(userName);
				if(StringUtils.hasText(token)) {
					super.setAttribute(request, "lineNotifyToken", token);
					super.setAttribute(request, "lineNotifyUserName", userName);
					vo = new AjaxStatusVO(AjaxStatusCode.Success);
				}
			}
		} catch(Exception e) {
			log.info("LineNotifyController.submitUserCode() failed : " + e, e);
		}
		return vo.toJsonString();
	}
	
	@RequestMapping("/loginLine")
	public void loginLine(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.sendRedirect(this.buileLineNotifyOauthUrl(request.getSession().getId()));
		} catch (IOException e) {
			log.info("LineNotifyController.loginLine() failed : " + e, e);
		}
	}
	
	@RequestMapping("/callback")
	public ModelAndView notifyCallback(HttpServletRequest request, 
									   @RequestParam(value="code")String code, 
									   @RequestParam(value="state")String state) {
		log.info("line callback Code = {}, state = {}, sessionId = {}", code, state, request.getSession().getId());
		try {
			String sessionId = CryptoUtils.decrypt(state);
			if(request.getSession().getId().equals(sessionId)) {
				String accessToken = this.getAccessToken(code);
				if(StringUtils.hasText(accessToken)) {
					String token;
					String userName = this.findUsernameByUsercode(code);
					if(StringUtils.hasText(userName)) {
						token = this.findNotifyToken(userName);
						if(!token.equals(om.readTree(accessToken).get("access_token").asText())) {
							//excel的token與line回傳不一樣時,新增一筆
							this.insertToGoogleSheet(userName, code, accessToken);
						}
					} else {
						token = om.readTree(accessToken).get("access_token").asText();
						userName = String.valueOf(new Random().nextInt(10000, 100000));
						this.insertToGoogleSheet(userName, code, accessToken);
					}
					super.setAttribute(request, "lineNotifyToken", token);
					super.setAttribute(request, "lineNotifyUserName", userName);
				}			
			}			
		} catch(Exception e) {
			log.info("LineNotifyController.notifyCallback() failed : " + e, e);
		}
		return new ModelAndView("redirect:/line/notify");
	}
	
	@RequestMapping("/sendNotify")
	public ModelAndView sendNotify(HttpServletRequest request, @RequestParam(value="msg", required=false)String msg) {
		try {
			Object token = super.getAttribute(request, MyRestConstants.SESS_ATTR_COMMON, "lineNotifyToken");
			if(token == null) {
				return new ModelAndView("redirect:/line/notify");
			} else {
				this.sendNotifyToLine(token.toString(), msg);
			}
		} catch(Exception e) {
			log.info("LineNotifyController.sendNotify() failed : " + e, e);
		}
		return new ModelAndView("pages/LineNotify");
	}	
	
	private String findNotifyToken(String userName) {
		log.info("LineNotifyController.findNotifyToken() :userName=" + userName);
		ObjectNode body = om.createObjectNode();
		body.put("action", "getByUserName");
		body.put("userName", userName);
		
		String result = "";
		try {
			String jsonStr = this.doGoogleAppScript(body);
			if(StringUtils.hasText(jsonStr)) {
		    	result = om.readTree(jsonStr).get("access_token").asText();				
			}
		} catch(Exception e) {
			log.info("LineNotifyController.findNotifyToken() faild : " + e, e);
		}
		return result;
	}
	
	private String buileLineNotifyOauthUrl(String sessionId) {
		String url = "https://notify-bot.line.me/oauth/authorize?";
		url += "response_type=code&";
		url += "client_id="+lineNotifyProp.getClient_id()+"&";
		url += "redirect_uri="+lineNotifyProp.getRedirect_uri()+"&";
		url += "scope=notify&";
		url += "state="+CryptoUtils.encrypt(sessionId)+"&";
		url += "response_mode=form_post";
		return url;
	}
	
	private String getAccessToken(String code) {
		String url = "https://notify-bot.line.me/oauth/token";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap body = new LinkedMultiValueMap();
		body.add("grant_type", "authorization_code");
		body.add("code", code);
		body.add("redirect_uri", lineNotifyProp.getRedirect_uri());
		body.add("client_id", lineNotifyProp.getClient_id());
		body.add("client_secret", lineNotifyProp.getClient_secret());

		String result = "";
        try {
        	result = gasRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity(body, headers), String.class).getBody();
        } catch (HttpServerErrorException e) {
            log.info("LineNotifyController.getAccessToken() failed : " + e, e);
        } catch (HttpClientErrorException e) {
        	log.info("LineNotifyController.getAccessToken() failed : " + e, e);
        }
		return result;
	}
	
	private void sendNotifyToLine(String token, String msg) {
		log.debug("LineNotifyController.sendNotifyToLine() : token=" + token);
		String url = "https://notify-api.line.me/api/notify";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setBearerAuth(token);
		MultiValueMap body = new LinkedMultiValueMap();
		body.add("message", msg);
        try {
        	String result = gasRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity(body, headers), String.class).getBody();
        	log.debug("LineNotifyController.sendNotifyToLine() : " + result);
        } catch (HttpServerErrorException e) {
            log.info("LineNotifyController.sendNotifyToLine() failed : " + e, e);
        } catch (HttpClientErrorException e) {
        	log.info("LineNotifyController.sendNotifyToLine() failed : " + e, e);
        }		
	}
	
	private void insertToGoogleSheet(String userName, String userCode, String token) {
		log.info("LineNotifyController.insertToGoogleSheet() :userName=" + userName + ",code=" + userCode + ",token=" + token);
		ObjectNode body = om.createObjectNode();
		body.put("action", "insert");
		body.put("userName", userName);
		body.put("userCode", userCode);
		body.put("token", token);
		this.doGoogleAppScript(body);
	}
	
	private String doGoogleAppScript(JsonNode json) {
		String result = "";
		try {
        	result = gasRestTemplate.exchange(lineNotifyProp.getGas_line_notify(), HttpMethod.POST, new HttpEntity(json.toString()), String.class).getBody();
        	log.debug("LineNotifyController.doGoogleAppScript() : " + result);
		} catch (HttpServerErrorException e) {
        	log.info("LineNotifyController.doGoogleAppScript() failed : " + e, e);
        } catch (HttpClientErrorException e) {
        	log.info("LineNotifyController.doGoogleAppScript() failed : " + e, e);
        }
		return result;
	}
	
	private String findUsernameByUsercode(String userCode) {
		ObjectNode body = om.createObjectNode();
		body.put("action", "getByUserCode");
		body.put("userCode", userCode);

		String result = "";
		try {
			String userName = this.doGoogleAppScript(body);
			if(StringUtils.hasText(userName)) {
		    	result = userName;				
			}
		} catch(Exception e) {
			log.info("LineNotifyController.findUsernameByUsercode() faild : " + e, e);
		}
		return result;
	}
	
	private boolean recaptchaTokenVerified(HttpServletRequest request, String recaptchaToken) {
		recaptchaToken = CryptoUtils.decrypt(recaptchaToken);
		log.info("recaptchaTokenVerified... token = {}", recaptchaToken);
		try {
			String[] token = recaptchaToken.split(">");
			return request.getSession().getId().equals(token[0]) & super.getRequestIp(request).equals(token[1]);
		} catch(Exception e) {
			return false;
		}
	}
}
