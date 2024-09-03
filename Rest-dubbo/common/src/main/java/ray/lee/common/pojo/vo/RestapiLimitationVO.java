package ray.lee.common.pojo.vo;

import java.util.Date;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import ray.lee.common.pojo.BasePojo;

@Data
public class RestapiLimitationVO extends BasePojo {
	private AtomicInteger counter = new AtomicInteger(0);
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Taipei")
	private Date latestApiTime;
	private ConcurrentSkipListSet<String> apiRecords = new ConcurrentSkipListSet();
}
