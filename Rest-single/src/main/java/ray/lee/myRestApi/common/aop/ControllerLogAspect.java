package ray.lee.myRestApi.common.aop;

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

@Component
@Aspect
public class ControllerLogAspect extends AopLogger {
	private long controllerStart;
	
	@Pointcut(value = "execution(* ray.lee.myRestApi.base.components.BaseController+.*(..)) || " + 
			  		  "execution(* ray.lee.myRestApi.controller.utilities.ControllerExceptionAdvice.*(..))")
	private void controllerLogPointcut() {
	
	}	
	
	@Before(value = "controllerLogPointcut()")
	public void beforeController(JoinPoint joinPoint) {
		this.controllerStart = System.currentTimeMillis();
		
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String classMethod = joinPoint.getSignature().getName();
        super.logger(LoggerFactory.getLogger(joinPoint.getTarget().getClass()), 
					 MessageFormat.format("{0}.{1}() start...", className, classMethod),
					 joinPoint.getArgs());
	}
	
	@After(value = "controllerLogPointcut()")
	public void afterController(JoinPoint joinPoint) {
		long timeDiff = Duration.ofMillis(System.currentTimeMillis() - controllerStart).toMillis();
		
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String classMethod = joinPoint.getSignature().getName();    	
        super.logger(LoggerFactory.getLogger(joinPoint.getTarget().getClass()), 
    				 MessageFormat.format("{0}.{1}() Time used:{2} ms", className, classMethod, timeDiff), 
    				 null);		
	}
	
    @AfterReturning(value = "controllerLogPointcut()")
    public void afterReturningController(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String classMethod = joinPoint.getSignature().getName();    	
        super.logger(LoggerFactory.getLogger(joinPoint.getTarget().getClass()), 
    				 MessageFormat.format("{0}.{1}() finish.", className, classMethod), 
    				 null);
    }
    
    @AfterThrowing(value = "controllerLogPointcut()", throwing = "ex")
    public void afterThrowingController(JoinPoint joinPoint, Exception ex) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String classMethod = joinPoint.getSignature().getName();    	
        super.logger(LoggerFactory.getLogger(joinPoint.getTarget().getClass()), 
    				 MessageFormat.format("{0}.{1}() exception={2}", className, classMethod, ex.getMessage()), 
    				 null);
    }
}
