package com.datfusrental.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

public class BaseDao<T> {

    @PersistenceContext(unitName = "PERSISTENCE")
    protected EntityManager entityManager;

    public T persist(T obj) {
    	entityManager.persist(obj);
    	return obj;
    }

    public T update(T obj) {
        return entityManager.merge(obj);
    }
    
    public void delete(T obj) {
        entityManager.remove(obj);
    }
    

	public EntityManager getEntityManager() {
		return this.entityManager;
	}
	
	@Transactional
	public Session getSession() {
		Session session = this.entityManager.unwrap(Session.class);
		return session;
	}
	
}
