package ray.lee.myRestApi.common.pojo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ray.lee.myRestApi.base.components.BasePojo;

@Schema(description = "訂單response物件")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse extends BasePojo {
	@Schema(description = "訂單編號")
	private int orderId;
	@Schema(description = "產品編號")
	private int productId;
	@Schema(description = "數量")
	private int quantity;
	@Schema(description = "總金額")
	private int totalAmount;
	@Schema(description = "訂單時間", format = "date-time")
	private String orderDate;
}
