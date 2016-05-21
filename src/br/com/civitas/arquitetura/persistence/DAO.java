package br.com.civitas.arquitetura.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public abstract class DAO {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	protected Session getSession(){
		return sessionFactory.getCurrentSession();
	}

	protected JdbcTemplate getJdbcTemplate(){
		return jdbcTemplate;
	}

}
