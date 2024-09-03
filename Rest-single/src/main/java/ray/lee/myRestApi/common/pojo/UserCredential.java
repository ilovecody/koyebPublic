package ray.lee.myRestApi.common.pojo;

import java.io.Serializable;

import lombok.Data;
import ray.lee.myRestApi.model.Enum.UserRole;

//UserCredential or UserSession
@Data
public class UserCredential implements Serializable {
	private int userId;
	private String userName;
	private UserRole userRole;
}