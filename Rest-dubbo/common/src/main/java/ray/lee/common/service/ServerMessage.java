package ray.lee.common.service;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerMessage implements Serializable {
	private static final long serialVersionUID = 75084218778687300L;
	public static int STATUS_SUCCESS = 1;
	public static int STATUS_FAILURE = 2;

	private int status = STATUS_SUCCESS;
	private String code;
	private String description;

	public ServerMessage(int status) {
		this.status = status;
	}	
	
	public ServerMessage(int status, String code) {
		this.status = status;
		this.code = code;
	}
	
	public boolean isSuccess() {
		return (this.status == STATUS_SUCCESS);
	}
}