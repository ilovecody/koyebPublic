package ray.lee.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ray.lee.common.components.RequestBodyValidationGroup.Get;

@Slf4j
@Data
public abstract class PageablePojo extends BasePojo {
	@Schema(description = "頁數", defaultValue = "1", type = "integer", requiredMode = RequiredMode.REQUIRED)
	@Min(value = 1, message = "PageablePojo.pageNumber.min", groups = {Get.class})
	private int pageNumber = 1;
	@Schema(description = "每頁筆數", defaultValue = "10", type = "integer", requiredMode = RequiredMode.REQUIRED)
	@Min(value = 10, message = "PageablePojo.pageSize.min", groups = {Get.class})
	private int pageSize = 10;
	//private long totalRecords;
}
