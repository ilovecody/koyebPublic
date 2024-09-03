package ray.lee.myRestApi.common.pojo;

import lombok.Data;
import ray.lee.myRestApi.base.components.BasePojo;

@Data
public class AjaxStatusVO extends BasePojo {
	public enum AjaxStatusCode {
		Failed(-1),
		Success(0);
		
		private int code;
		AjaxStatusCode(int code) {
			this.code = code;
		}
		
		public int getCode() {
			return this.code;
		}
	};
	
	private int statusCode;
	private Object jsonData;
	private String stringData;	
	
	public AjaxStatusVO(AjaxStatusCode statusCode) {
		this.statusCode = statusCode.getCode();
	}
}
