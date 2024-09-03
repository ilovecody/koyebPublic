package ray.lee.myRestApi.controller.utilities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.annotation.Resource;
import ray.lee.common.pojo.vo.GooglereCaptchaResponseVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Objects;

@Component
public class GoogleRecaptchaHelper {
    private static final Logger log = LoggerFactory.getLogger(GoogleRecaptchaHelper.class);
    private static final String verifyUrl = "https://www.google.com/recaptcha/api/siteverify";
	private ObjectMapper om = new ObjectMapper();
    @Resource(name="gasRestTemplate")
	private RestTemplate gasRestTemplate;    
    @Value(value = "${google.recaptcha.sitekey}")
    private String sitekey;
    @Value(value = "${google.recaptcha.secret}")
    private String secret;
    

    public String getSitekey() {
        return sitekey;
    }

    public GooglereCaptchaResponseVO verify(String ip, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    	UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(verifyUrl)
				.queryParam("secret", secret)
				.queryParam("response", token)
				.queryParam("remoteip", ip);
        
        ResponseEntity<GooglereCaptchaResponseVO> response = gasRestTemplate.exchange(builder.build().toUriString(), HttpMethod.POST, new HttpEntity(headers), GooglereCaptchaResponseVO.class);
        if (response.getStatusCode().is2xxSuccessful()) {
        	try {
				log.debug("GoogleRecaptchaHelper.verify() result={}", om.writeValueAsString(response.getBody()));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
        	return response.getBody();
        } else {
        	GooglereCaptchaResponseVO failed = new GooglereCaptchaResponseVO();
        	return failed;
        }
    }
}
