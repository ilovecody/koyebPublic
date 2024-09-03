package ray.lee.myRestApi.services;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.aop.framework.AopContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import ray.lee.myRestApi.base.components.ServerContext;
import ray.lee.myRestApi.base.components.ServerMessage;
import ray.lee.myRestApi.base.components.ServiceInterface;
import ray.lee.myRestApi.common.exception.MyRestApiException;
import ray.lee.myRestApi.common.exception.ResourceNotFoundException;
import ray.lee.myRestApi.common.pojo.UserCredential;
import ray.lee.myRestApi.common.pojo.request.ListOrderRequest;
import ray.lee.myRestApi.common.pojo.request.OrderRequest;
import ray.lee.myRestApi.common.pojo.response.ListOrderResponse;
import ray.lee.myRestApi.common.pojo.response.OrderResponse;
import ray.lee.myRestApi.common.utilities.MessageCode;
import ray.lee.myRestApi.dao.OrderDao;
import ray.lee.myRestApi.model.Order;

@Service
public class OrderService implements ServiceInterface {
	@Resource
	private OrderDao orderDao;

	@Override
	public ServerContext doService(ServerContext ctx) throws Exception {
		switch(ctx.getActionId()) {
			case order_create :
				return ((OrderService)AopContext.currentProxy()).createOrder(ctx);
			case order_delete :
				return ((OrderService)AopContext.currentProxy()).deleteOrder(ctx);
			case order_updateQuantity :
				return ((OrderService)AopContext.currentProxy()).updateQuantity(ctx);
			case order_update :
				return ((OrderService)AopContext.currentProxy()).updateOrder(ctx);
			case order_getByOrderId :
				return this.getByOrderId(ctx);
			case order_listOrders :
				return this.listOrders(ctx);
			case order_reset :
				return ((OrderService)AopContext.currentProxy()).resetOrder(ctx);
		}
		return null;
	}

	@Transactional(rollbackFor = Exception.class)
	public ServerContext createOrder(ServerContext ctx) {
		OrderRequest orderForm = ctx.getRequestParameter(OrderRequest.class);
		//do something...
		Order order = new Order();
		order.setProductId(orderForm.getProductId());
		order.setTotalAmount(orderForm.getTotalAmount());
		order.setQuantity(orderForm.getQuantity());
		order.setUserId(ctx.getUser().getUserId()+"");
		order.setOrderDate(new Date());
		order = orderDao.createOrder(order);
		
		OrderResponse res = this.orderTransfer(order);
		ctx.setReturnMessage(new ServerMessage());
		ctx.setResponseResult(res);
		return ctx;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public ServerContext deleteOrder(ServerContext ctx) throws Exception {
		int orderId = ctx.getRequestParameter(Integer.class);
		Order order = orderDao.findOrderById(orderId, false);
		if(order == null) {
			throw new ResourceNotFoundException(MessageCode.Order_NotFound);
		} else {
			order.setDelete(true);
			orderDao.updateOrder(order);
			ctx.setReturnMessage(new ServerMessage());
			return ctx;
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	public ServerContext updateQuantity(ServerContext ctx) throws Exception {
		OrderRequest orderRequest = ctx.getRequestParameter(OrderRequest.class);
		Order order = orderDao.findOrderById(orderRequest.getOrderId(), false);
		if(order == null) {
			throw new ResourceNotFoundException(MessageCode.Order_NotFound);
		} else {
			order.setQuantity(orderRequest.getQuantity());
			orderDao.updateOrder(order);
			ctx.setReturnMessage(new ServerMessage());
			return ctx;
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	public ServerContext updateOrder(ServerContext ctx) throws Exception {
		OrderRequest orderRequest = ctx.getRequestParameter(OrderRequest.class);
		Order order = orderDao.findOrderById(orderRequest.getOrderId(), false);
		if(order == null) {
			throw new ResourceNotFoundException(MessageCode.Order_NotFound);
		} else {
			order.setProductId(orderRequest.getProductId());
			order.setTotalAmount(orderRequest.getTotalAmount());
			order.setQuantity(orderRequest.getQuantity());
			orderDao.updateOrder(order);
			ctx.setReturnMessage(new ServerMessage());
			return ctx;
		}
	}
	
	public ServerContext getByOrderId(ServerContext ctx) throws Exception {
		int orderId = ctx.getRequestParameter(Integer.class);
		Order order = orderDao.findOrderById(orderId, false);
		if(order == null) {
			throw new ResourceNotFoundException(MessageCode.Order_NotFound);
		} else {
			OrderResponse res = this.orderTransfer(order);
			ctx.setReturnMessage(new ServerMessage());
			ctx.setResponseResult(res);
			return ctx;
		}
	}
	
	public ServerContext listOrders(ServerContext ctx) throws Exception {
		ListOrderRequest req = ctx.getRequestParameter(ListOrderRequest.class);
		UserCredential user = ctx.getUser();
		
		Page<Order> pageable = orderDao.listOrders(req.getPageNumber(), req.getPageSize(), false);
		ListOrderResponse res = new ListOrderResponse();
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(pageable.getTotalElements());
		res.setOrders(pageable.stream().map(o -> this.orderTransfer(o)).collect(Collectors.toList()));		
		
		ctx.setReturnMessage(new ServerMessage());
		ctx.setResponseResult(res);
		return ctx;
	}
	
	private OrderResponse orderTransfer(Order order) {
		OrderResponse res = new OrderResponse();
		res.setOrderId(order.getId());
		res.setProductId(order.getProductId());
		res.setQuantity(order.getQuantity());
		res.setTotalAmount(order.getTotalAmount());
		res.setOrderDate(order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().toString() + 
						 OffsetDateTime.now().getOffset().toString());
		return res;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public ServerContext resetOrder(ServerContext ctx) throws Exception {
		Map<Integer, Order> map = this.getDefaultOrders();
		orderDao.listOrders(1, 10, true).getContent().forEach(order -> {
			Order defaultOrder = map.get(order.getId());
			if(defaultOrder != null) {
				order.setProductId(defaultOrder.getProductId());
				order.setQuantity(defaultOrder.getQuantity());
				order.setTotalAmount(defaultOrder.getTotalAmount());
				order.setDelete(false);
				orderDao.updateOrder(order);
			}
		});
		return ctx;
	}
	
	private Map<Integer, Order> getDefaultOrders() {
		Map<Integer, Order> map = new HashMap();
		for(int i=1;i<=10;i++) {
			Order order = new Order();
			order.setDelete(false);
			order.setProductId(1010);
			if(i < 10) {
				order.setProductId(i*11);
			}
			order.setQuantity(1);
			order.setTotalAmount(i*1000);
			map.put(i, order);
		}
		return map;
	}
}
