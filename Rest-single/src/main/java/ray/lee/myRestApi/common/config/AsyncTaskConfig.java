package ray.lee.myRestApi.common.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class AsyncTaskConfig {
	private static final String ThreadNamePrefix_1 = "AsyncThreadPoolTaskExecutor - ";
	private static final String ThreadNamePrefix_2 = "SchedulerThreadPoolTaskExecutor - ";

    @Bean(name = "asyncTask")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(500);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix(ThreadNamePrefix_1);
        return executor;
    }
    
    @Bean
    public TaskScheduler taskScheduler() {
    	ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    	taskScheduler.setPoolSize(5);
    	taskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    	taskScheduler.setThreadNamePrefix(ThreadNamePrefix_2);
    	taskScheduler.initialize();
    	return taskScheduler;
    }    
}
