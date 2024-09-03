package ray.lee.myRestApi;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ray.lee.myRestApi.common.utilities.MyRestConstants;

@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableAsync
@EnableScheduling
public class MyRestApp extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    	return application.sources(MyRestApp.class);
    }
    
    public static void main(String[] args) {
    	TimeZone.setDefault(TimeZone.getTimeZone(MyRestConstants.SystemDefaultTimezone));
    	SpringApplication.run(MyRestApp.class, args);
    }
}
