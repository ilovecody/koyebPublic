package ray.lee.services.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

import jakarta.annotation.Nullable;
import ray.lee.model.Order;
import ray.lee.model.QOrder;

@Component
public class OrderDao extends BaseDao {

	public Order createOrder(Order order) {
		return super.insertEntity(order);
	}
	
	public Order findOrderById(int orderId, boolean isDelete) {
		QOrder order = QOrder.order;
		Predicate query = order.id.eq(orderId)
					 	  .and(order.isDelete.eq(isDelete));
		return super.findOneByPredicate(query, order);
	}
	
	public Order updateOrder(Order order) {
		return super.updateEntity(order);
	}
	
	public Page<Order> listOrders(int page, int size, boolean isDelete) {
		PageRequest pageRequest = PageRequest.of(page, size);
		QOrder order = QOrder.order;
		Predicate query = order.id.gt(0)
						  .and(order.isDelete.eq(isDelete));
		return super.findPageByPredicate(query, order, order.id.asc(), pageRequest);
	}
}
