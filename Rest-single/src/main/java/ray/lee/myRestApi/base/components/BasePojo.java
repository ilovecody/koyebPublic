package ray.lee.myRestApi.base.components;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;
import ray.lee.myRestApi.common.utilities.ObjectMapperUtils;

@Slf4j
@JsonIgnoreProperties(ignoreUnknown=true)
public abstract class BasePojo {
	
	public String toJsonString() {
		try {
			return ObjectMapperUtils.toJsonString(this);
		} catch (JsonProcessingException e) {
			log.debug("BasePojo.toJsonString() error -- {}", e.getMessage(), e);
			return "";
		}
	}
	
	public void constructorFromJsonString(String json, Class clazz) {
		try {
			Object obj = ObjectMapperUtils.toObject(json, clazz);
			List.of(clazz.getDeclaredFields()).forEach(field -> {
				field.setAccessible(true);
				try {
					field.set(this, field.get(obj));
				} catch(Exception e) {
					log.debug("BasePojo.constructorFromJsonString() error -- {}, class -- {}", e.getMessage(), clazz.getName());
				}
			});
		} catch (Exception e) {
			log.debug("BasePojo.constructorFromJsonString() error -- {}, class -- {}",e.getMessage(), clazz.getName(), e);
		}
	}
}
