package ray.lee.common.pojo.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ray.lee.common.pojo.BasePojo;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestToken implements Serializable  {
	private static final long serialVersionUID = -2610789864993334821L;
	private long timestamp;
	private String clientId;
	private String token;
}
