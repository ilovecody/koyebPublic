package ray.lee.myRestApi.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="my_order")
public class Order implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private int id;
	
	@Column
	private String userId;
	
	@Column
	private int productId;
	
	@Column
	private Integer totalAmount;
	
	@Column
	private int quantity;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date orderDate;
	
	@Column(nullable = false)
	private boolean isDelete;
}
