package ray.lee.common.service.Enum;

public enum ActionId {
	jwt_getToken,
	jwt_getUserByClientId,
	order_create,
	order_delete,
	order_update,
	order_updateQuantity,
	order_getByOrderId,
	order_listOrders,
	order_reset,
	LineBot_MessageEvent,
	LineBot_PushMessage;
}
