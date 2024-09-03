package ray.lee.common.pojo.vo;

import java.io.Serializable;

import lombok.Data;
import ray.lee.model.Enum.UserRole;

//UserCredential or UserSession
@Data
public class UserCredential implements Serializable {
	private static final long serialVersionUID = -391077292346857460L;
	private int userId;
	private String userName;
	private UserRole userRole;
}