package ray.lee.myRestApi.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import ray.lee.myRestApi.model.Enum.UserRole;

@Entity
@Table(name="jwt_user")
@Data
public class JwtUser implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private int id;
	
	@Column(nullable = false)
	private String clientId;
	
	@Column(nullable = false)
	private String clientSecret;
	
	@Column
	private String name;
	
	@Column
	@Enumerated(EnumType.STRING)
	private UserRole role;
}
