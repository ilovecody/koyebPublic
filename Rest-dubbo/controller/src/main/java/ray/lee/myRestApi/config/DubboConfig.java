package ray.lee.myRestApi.config;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.ReferenceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ray.lee.common.service.ServiceInterface;

@Configuration
public class DubboConfig {
    @Bean
    @DubboReference(timeout = 5*1000)
    public ReferenceBean<ServiceInterface> dubboService() {
        return new ReferenceBean();
    }
}
