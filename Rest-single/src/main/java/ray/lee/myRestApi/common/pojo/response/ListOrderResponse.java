package ray.lee.myRestApi.common.pojo.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ray.lee.myRestApi.common.pojo.PageablePojo;

@Schema(description = "訂單列表物件")
@Data
public class ListOrderResponse extends PageablePojo {
	@Schema(description = "訂單總筆數")
	private long totalRecords;
	private List<OrderResponse> orders;
}
