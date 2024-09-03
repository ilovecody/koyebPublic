package ray.lee.myRestApi.base.components;

import java.util.List;

import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Component;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContexts;
import ray.lee.myRestApi.model.Order;
import ray.lee.myRestApi.model.QJwtUser;
import ray.lee.myRestApi.model.QOrder;

public abstract class BaseDao {
	@PersistenceContext
	protected EntityManager entityManager;
	protected JPAQueryFactory jPAQueryFactory;
	
	@PostConstruct
	public void init() {
		this.jPAQueryFactory = new JPAQueryFactory(entityManager);
	}

	protected <T> T updateEntity(T entity) {
		entityManager.merge(entity);
		return (T)entity;
	}
	
	protected <T> T insertEntity(T entity) {
		entityManager.persist(entity);
		return (T)entity;
	}
	
	protected void deleteEntity(Object entity) {
		entityManager.remove(entity);
	}
	
	protected void deleteEntity(List<Object> entityList) {
		entityList.forEach(entity -> entityManager.remove(entity));
	}	
	
	protected Session getHibernateSession() {
		return entityManager.unwrap(Session.class);
	}
	
	protected <T> T findOneByPredicate(Predicate query, EntityPath<T> entity) {
		return this.findOneByPredicate(query, entity, null, null);
	}
	
	protected <T> T findOneByPredicate(Predicate query, EntityPath<T> entity, OrderSpecifier orderBy) {
		return this.findOneByPredicate(query, entity, orderBy, null);
	}	
	
	protected <T> T findOneByPredicate(Predicate query, EntityPath<T> entity, Expression groupBy) {
		return this.findOneByPredicate(query, entity, null, groupBy);
	}	
	
	protected <T> T findOneByPredicate(Predicate query, EntityPath<T> entity, OrderSpecifier orderBy, Expression groupBy) {
		return (T) this.buildJpaQuery(query, entity, orderBy, groupBy).fetchFirst();
	}
	
	protected <T> List<T> listByPredicate(Predicate query, EntityPath<T> entity) {
		return this.buildJpaQuery(query, entity, null, null).fetch();
	}	
	
	protected <T> List<T> listByPredicate(Predicate query, EntityPath<T> entity, OrderSpecifier orderBy) {
		return this.buildJpaQuery(query, entity, orderBy, null).fetch();
	}	
	
	protected <T> List<T> listByPredicate(Predicate query, EntityPath<T> entity, Expression groupBy) {
		return this.buildJpaQuery(query, entity, null, groupBy).fetch();
	}	
	
	protected <T> List<T> listByPredicate(Predicate query, EntityPath<T> entity, OrderSpecifier orderBy, Expression groupBy) {
		return this.buildJpaQuery(query, entity, orderBy, groupBy).fetch();
	}
	
	protected Object queryByNativeSql(String nativeSql) {
		return entityManager.createNativeQuery(nativeSql).getSingleResult();
	}
	
	protected List queryListByNativeSql(String nativeSql) {
		return entityManager.createNativeQuery(nativeSql).getResultList();
	}
	
	protected <T> Page<T> findPageByPredicate(Predicate query, EntityPath<T> entity, OrderSpecifier orderBy, PageRequest pageRequest) {
		pageRequest = pageRequest.withPage(pageRequest.getPageNumber()-1);
		if(orderBy != null) {
			pageRequest = pageRequest.withSort(QSort.by(orderBy));	
		}
		
		long totalRecords = jPAQueryFactory.select(Expressions.numberOperation(Long.class, Ops.AggOps.COUNT_AGG, entity))
											.from(entity)
											.where(query)
											.fetchFirst();
		List<T> result = this.buildPageableJpaQuery(query, entity, orderBy, pageRequest).fetch();
		return this.buildPageImpl(result, pageRequest, totalRecords);
	}
	
	protected <T> Page<T> findPageByNativeSql(String countQuery, String nativeSql, EntityPath<T> entity, PageRequest pageRequest) {
		pageRequest = pageRequest.withPage(pageRequest.getPageNumber()-1);
		int totalRecords = this.queryListByNativeSql(countQuery).size();
		List result = this.queryListByNativeSql(nativeSql);
		return this.buildPageImpl(result, pageRequest, totalRecords);
	}
	
	private <T> JPAQuery<T> buildJpaQuery(Predicate query, EntityPath<T> entity, OrderSpecifier orderBy, Expression groupBy) {
		JPAQuery jpaQuery = jPAQueryFactory.selectFrom(entity).where(query);
		if(orderBy != null) {
			jpaQuery.orderBy(orderBy);
		}
		if(groupBy != null) {
			jpaQuery.groupBy(groupBy);	
		}
		return jpaQuery;
	}
	
	private <T> JPAQuery<T> buildPageableJpaQuery(Predicate query, EntityPath<T> entity, OrderSpecifier orderBy, PageRequest pageRequest) {
		JPAQuery jpaQuery = this.buildJpaQuery(query, entity, orderBy, null);
		jpaQuery.offset(pageRequest.getOffset());
		jpaQuery.limit(pageRequest.getPageSize());
		return jpaQuery;
	}
	
	private <T> Page<T> buildPageImpl(List entityResult, PageRequest pageRequest, long totalRecords) {
		return new PageImpl<T>(entityResult, pageRequest, totalRecords);
	}
}
