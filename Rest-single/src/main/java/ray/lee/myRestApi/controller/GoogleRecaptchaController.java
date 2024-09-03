package ray.lee.myRestApi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import ray.lee.myRestApi.base.components.BaseController;
import ray.lee.myRestApi.common.pojo.AjaxStatusVO;
import ray.lee.myRestApi.common.pojo.AjaxStatusVO.AjaxStatusCode;
import ray.lee.myRestApi.common.pojo.GooglereCaptchaResponseVO;
import ray.lee.myRestApi.common.utilities.CryptoUtils;
import ray.lee.myRestApi.controller.utilities.GoogleRecaptchaHelper;

@Controller
@RequestMapping("/google")
public class GoogleRecaptchaController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(GoogleRecaptchaController.class);
	@Resource
	private GoogleRecaptchaHelper reCaptchaHelper;

	@RequestMapping("/recaptcha")
	@ResponseBody
	public String recaptcha(HttpServletRequest request, @RequestParam("token")String token) {
		//log.debug("GoogleRecaptchaController.recaptcha()...");
		GooglereCaptchaResponseVO response = reCaptchaHelper.verify(super.getRequestIp(request), token);
		if(response.getScore() > 0.5f) {
			AjaxStatusVO vo = new AjaxStatusVO(AjaxStatusCode.Success);
			vo.setStringData(this.recaptchaSuccessTokenGenerator(request));
			return vo.toJsonString();
		} else {
			return new AjaxStatusVO(AjaxStatusCode.Failed).toJsonString();
		}
	}
	
	private String recaptchaSuccessTokenGenerator(HttpServletRequest request) {
		String token = request.getSession().getId();
		token += ">" + super.getRequestIp(request);
		token += ">" + System.currentTimeMillis();
		return CryptoUtils.encrypt(token);
	}	
}
