package ray.lee.services.aop;

import java.text.MessageFormat;
import java.time.Duration;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ray.lee.common.logger.AopLogger;
import ray.lee.common.service.ServerContext;

@Component
@Aspect
public class ServiceLogAspect extends AopLogger {
	private long serviceStart;

	@Pointcut(value = "execution(* ray.lee.common.service..ServiceInterface+.*(..))")
	private void serviceLogPointcut() {
		
	}
	
	@Before(value = "serviceLogPointcut()")
	public void beforeServices(JoinPoint joinPoint) {
		this.serviceStart = System.currentTimeMillis();
		
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String classMethod = joinPoint.getSignature().getName();
        super.logger(LoggerFactory.getLogger(joinPoint.getTarget().getClass()), 
					 MessageFormat.format("{0}.{1}({2}) start...", className, classMethod, this.getActionId(joinPoint)),
					 this.getSessionId(joinPoint),
					 null);
	}
	
	@After(value = "serviceLogPointcut()")
	public void afterServices(JoinPoint joinPoint) {
		long timeDiff = Duration.ofMillis(System.currentTimeMillis() - serviceStart).toMillis();
		
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String classMethod = joinPoint.getSignature().getName();    	
        super.logger(LoggerFactory.getLogger(joinPoint.getTarget().getClass()), 
    				 MessageFormat.format("{0}.{1}({2}) Time used:{3} ms", className, classMethod, this.getActionId(joinPoint), timeDiff), 
    				 this.getSessionId(joinPoint),
    				 null);		
	}
	
    @AfterReturning(value = "serviceLogPointcut()")
    public void afterReturningServices(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String classMethod = joinPoint.getSignature().getName();    	
        super.logger(LoggerFactory.getLogger(joinPoint.getTarget().getClass()), 
    				 MessageFormat.format("{0}.{1}({2}) finish.", className, classMethod, this.getActionId(joinPoint)), 
    				 this.getSessionId(joinPoint),
    				 null);
    }
    
    @AfterThrowing(value = "serviceLogPointcut()", throwing = "ex")
    public void afterThrowingServices(JoinPoint joinPoint, Exception ex) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String classMethod = joinPoint.getSignature().getName();    	
    	super.logger(LoggerFactory.getLogger(joinPoint.getTarget().getClass()), 
    				 MessageFormat.format("{0}.{1}({2}) exception={3}", className, classMethod, this.getActionId(joinPoint), ex.getMessage()), 
    				 this.getSessionId(joinPoint),
    				 null);
    }
    
    private String getActionId(JoinPoint joinPoint) {
    	try {
    		return ((ServerContext)joinPoint.getArgs()[0]).getActionId().toString();	
    	} catch(Exception e) {
    		return "getActionId failed:"+e.getMessage();
    	}
    }
    
    private String getSessionId(JoinPoint joinPoint) {
    	try {
    		return ((ServerContext)joinPoint.getArgs()[0]).getSessionId();	
    	} catch(Exception e) {
    		return "getSessionId failed:"+e.getMessage();
    	}
    }    
}
