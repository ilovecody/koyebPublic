package ray.lee.myRestApi.common.pojo;

import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import ray.lee.myRestApi.base.components.BasePojo;
import ray.lee.myRestApi.common.utilities.MyRestConstants;

@Data
public class RestapiLimitationVO extends BasePojo {
	private AtomicInteger counter = new AtomicInteger(0);
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = MyRestConstants.SystemDefaultTimezone)
	private Date latestApiTime;
	private ConcurrentSkipListSet<String> apiRecords = new ConcurrentSkipListSet();
}
