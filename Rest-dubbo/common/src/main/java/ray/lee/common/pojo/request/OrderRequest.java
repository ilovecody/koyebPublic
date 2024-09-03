package ray.lee.common.pojo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ray.lee.common.components.RequestBodyValidationGroup.Post;
import ray.lee.common.components.RequestBodyValidationGroup.Put;
import ray.lee.common.pojo.BasePojo;

@Schema(description = "訂單request物件")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest extends BasePojo {
	@Schema(description = "訂單編號")
	@Min(value = 1, message = "OrderRequest.orderId.Min", groups = {Put.class})
	private int orderId;
	@Schema(description = "產品編號")
	@Min(value = 1, message = "OrderRequest.productId.Min", groups = {Post.class, Put.class})
	private int productId;
	@Schema(description = "數量")
	@Min(value = 1, message = "OrderRequest.quantity.Min", groups = {Post.class, Put.class})
	private int quantity;
	@Schema(description = "總金額")
	@Min(value = 1, message = "OrderRequest.totalAmount.Min", groups = {Post.class, Put.class})
	private int totalAmount;
}
