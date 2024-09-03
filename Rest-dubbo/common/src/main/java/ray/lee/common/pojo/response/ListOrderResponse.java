package ray.lee.common.pojo.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ray.lee.common.pojo.PageablePojo;

@Schema(description = "訂單列表物件")
@Data
public class ListOrderResponse extends PageablePojo {
	//本來應該放在PageablePojo裡,但因應swagger ui的param參數,所以搬到response
	@Schema(description = "訂單總筆數")
	private long totalRecords;
	private List<OrderResponse> orders;
}
