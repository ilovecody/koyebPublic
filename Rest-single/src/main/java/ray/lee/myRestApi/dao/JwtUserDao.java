package ray.lee.myRestApi.dao;

import org.springframework.stereotype.Component;

import ray.lee.myRestApi.base.components.BaseDao;
import ray.lee.myRestApi.model.JwtUser;
import ray.lee.myRestApi.model.QJwtUser;

@Component
public class JwtUserDao extends BaseDao {
	
	public JwtUser createJwtUser(JwtUser user) {
		return super.insertEntity(user);
	}
	
	public JwtUser findByClientId(String clientId) {
		return jPAQueryFactory.selectFrom(QJwtUser.jwtUser)
							.where(QJwtUser.jwtUser.clientId.eq(clientId))
							.fetchOne();
	}
}
