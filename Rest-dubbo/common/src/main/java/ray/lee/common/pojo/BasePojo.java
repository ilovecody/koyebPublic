package ray.lee.common.pojo;

import java.io.Serializable;
import java.util.List;

import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@JsonIgnoreProperties(ignoreUnknown=true)
public abstract class BasePojo implements Serializable {
	private static final long serialVersionUID = 8420734057067019338L;

	public String toJsonString() {
		try {
			return ObjectMapperUtil.getObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			log.debug("BasePojo.toJsonString() error -- {}", e.getMessage(), e);
			return "";
		}
	}
	
	public void constructorFromJsonString(String json, Class clazz) {
		try {
			Object obj = ObjectMapperUtil.getObjectMapper().readValue(json, clazz);
			List.of(clazz.getDeclaredFields()).forEach(field -> {
				field.setAccessible(true);
				try {
					field.set(this, field.get(obj));
				} catch (Exception e) {
					log.debug("BasePojo.constructorFromJsonString() error -- {}, class -- {}", e.getMessage(), clazz.getName());
				}
			});
		} catch (Exception e) {
			log.debug("BasePojo.constructorFromJsonString() error -- {}, class -- {}",e.getMessage(), clazz.getName(), e);
		}
	}
	
	private static class ObjectMapperUtil {
		private static ObjectMapper om = new ObjectMapper();
		
		public static ObjectMapper getObjectMapper() {
			return om;
		}
	}
}
