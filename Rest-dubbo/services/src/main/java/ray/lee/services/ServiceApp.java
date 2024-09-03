package ray.lee.services;

import java.util.TimeZone;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ray.lee.services.dubbo.DubboServiceProvider;

@SpringBootApplication
//@ServletComponentScan
@EntityScan(basePackages="ray.lee.model")
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableAsync
@EnableScheduling
@EnableDubbo
public class ServiceApp {
	private static final String SystemDefaultTimezone = "Asia/Taipei";
    
    public static void main(String[] args) {
    	TimeZone.setDefault(TimeZone.getTimeZone(SystemDefaultTimezone));
    	SpringApplication.run(ServiceApp.class, args);
    }
}
