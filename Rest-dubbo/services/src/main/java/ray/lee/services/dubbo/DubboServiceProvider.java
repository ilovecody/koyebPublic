package ray.lee.services.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import ray.lee.common.constants.MessageCode;
import ray.lee.common.exceptions.MyRestApiException;
import ray.lee.common.service.ServerContext;
import ray.lee.common.service.ServiceInterface;

@DubboService(timeout = 5*1000)
public class DubboServiceProvider implements ServiceInterface {
	@Resource
	private ApplicationContext applicationContext;
	
	@Override
	public ServerContext doService(ServerContext ctx) throws Exception {
		if(ctx == null) {
			throw new MyRestApiException(MessageCode.Exception, "DubboServiceProvider.doService() failed -- ServerContext is null");
		} else if(ctx.getServiceId() == null) {
			throw new MyRestApiException(MessageCode.Exception, "DubboServiceProvider.doService() failed -- ServiceId is null");	
		} else if (ctx.getActionId() == null) {
			throw new MyRestApiException(MessageCode.Exception, "DubboServiceProvider.doService() failed -- actionId is null");
		}		
		
		ServiceInterface service = applicationContext.getBean(ctx.getServiceId().toString(), ServiceInterface.class);
		if(service == null) {
			throw new MyRestApiException(MessageCode.Exception, "Service not found: "+ctx.getServiceId());
		}
		return service.doService(ctx);
	}
}
