package br.com.civitas.arquitetura.persistence;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.arquitetura.annotations.OrderBy;
import br.com.civitas.arquitetura.entity.Entidade;
import br.com.civitas.arquitetura.entity.IEntity;
import br.com.civitas.arquitetura.service.ServiceCrud;


@Transactional
@Repository
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class AbstractPersistence<E extends Entidade> implements ServiceCrud<E> {
 
	private static final long serialVersionUID = 33975962866715227L;
	public static final String USUARIO = "usuario";

	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	protected Session getSession(){
		return getSessionFactory().getCurrentSession();
	}

	protected abstract Class getClazz();
	
	public E save(E object){
		getSession().saveOrUpdate(object);
		return object;
	}

	public E insert(E object) {
		 getSession().save(object);
		 return object;
	}

	@Transactional(rollbackFor = HibernateException.class)
	public void insertAll(Collection<E> collection) {
		for(Entidade object : collection){
			getSession().saveOrUpdate(object);
		}
	}

	public E update(E object) {
		getSession().saveOrUpdate(object);
		return object;
	}

	@Transactional(rollbackFor = HibernateException.class)
	public void updateAll(Collection<E> collection) {
		for(Entidade object : collection){
			getSession().saveOrUpdate(object);
		}
	}

	public void saveOrUpdateAll(Collection<E> collection) {
		updateAll(collection);
	}

	public void remove(E object) {
		getSession().delete(object);
	}

	@Transactional(rollbackFor = HibernateException.class)
	public void removeAll(Collection<E> collection) {
		for(Entidade object : collection){
			getSession().delete(object);
		}
	}

	public E findByPrimaryKey(Class<E> clazz, Serializable id) {
		return (E) getSession().get(clazz, id);
	}

	public List<E> findAll(Class<E> clazz) {
		if (clazz.isAnnotationPresent(OrderBy.class)) {
			String campoOrdenacao = ((OrderBy) clazz.getAnnotation(OrderBy.class)).value();
			if (campoOrdenacao != null && !"".equals(campoOrdenacao)) {
				return findAll(clazz, campoOrdenacao);
			} 
		} 
		
		return (List<E>) getSession().createCriteria(getClazz()).list();
	}
  
	public static boolean checkIsNotNull(Object checkedObject) {
		boolean response = checkedObject!= null;

		if(checkedObject instanceof List<?>){
			response  = !((((List<IEntity>)checkedObject).isEmpty()) || (((List<IEntity>)checkedObject).get(0) == null));
		}
		
		return response;
	}
	
   public boolean checkIsNull(Object value){
    	return !checkIsNotNull(value);
    }
	
   public static String convertListToString(List<? extends IEntity> list){
		
 		if(list == null || list.isEmpty() || list.get(0) == null){
 			return "";
 		}
 		
 		if(list.get(0).getId() == -1L){
 			list.remove(0);
 		}
 		
 		List<IEntity> copy = new ArrayList<IEntity>();
 		copy.addAll(list);
 		
 		CollectionUtils.transform(copy, new BeanToPropertyValueTransformer("id"));
 		return copy.toString().replace("[", "").replace("]", "");
 	}
   
	public List<E> findAll(Class<E> clazz, String orderProperty) {
		StringBuilder query = new StringBuilder();
		
		query.append("from ").append(clazz.getName()).append(" order by ")
			.append(orderProperty);
		
		return (List<E>) getSession().createQuery(query.toString()).list();
	}

	public List<E> findByExample(E obj) {
		Example example = Example.create(obj).excludeZeroes();
		
		return (List<E>) getSession().createCriteria(getClass()).add(example).list();
	}

	public List<E> startsWith(Class<E> clazz, String strStart) {
		StringBuilder query = new StringBuilder();
		
		query.append("from ").append(clazz.getName())
			.append(" where descricao like '?%' or descricao like '?%'");
	
		
		return (List<E>) getSession().createQuery(query.toString())
				.setString(0, strStart.toLowerCase())
				.setString(1, strStart.toUpperCase())
				.list();
	}

	public List<E> startsWithAndFilter(Class<E> clazz, String strStart,
			Map<String, Object> filter) {
		
		StringBuilder query = new StringBuilder();
		query.append("from ").append(clazz.getName()).append(" where ( descricao like '")
			.append(strStart.toLowerCase())
			.append("%' or descricao like '")
			.append(strStart.toUpperCase() + "%' )" );
		
		Set<String> setKey = filter.keySet();
		for( String key : setKey ){
			Object valor = filter.get( key );
			
			if( valor instanceof String 
					|| valor instanceof Character ){
				query.append(" and ( ").append(key).append(" like '%")
					.append(valor.toString().toLowerCase())
					.append("%' or " + key + " like '%") 
					.append(valor.toString().toUpperCase())
					.append("%') ");
			}
			else if( valor instanceof Date ){
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				query.append(" and ").append(key).append(" = '").append(df.format(valor)).append("' ");
			}else{
				query.append(" and ").append(key).append(" = '").append(valor);
			}
			
			if (clazz.isAnnotationPresent(OrderBy.class)) {
				String ordenacao = ((OrderBy) clazz.getAnnotation(OrderBy.class)).value();
				if (ordenacao != null && !"".equals(ordenacao)) {
					query.append(" order by ").append(ordenacao);
				} 
			} 
			
		}		
		return (List<E>) getSession().createQuery(query.toString()).list();
	}

	public List<E> findByFilter(Class<E> clazz, Map<String, Object> filter) {
		String valor = null;
		
		if (clazz.isAnnotationPresent(OrderBy.class)) {
			valor = ((OrderBy) clazz.getAnnotation(OrderBy.class)).value();
		} 
		
		String query = montaQuery(clazz, filter, valor);	
		
		return (List<E>) getSession().createQuery(query).list();
	}

	public List<E> findByFilter(Class<E> clazz, Map<String, Object> filter,
			String campoOrdenacao) {
		String query = montaQuery(clazz, filter, campoOrdenacao);
		return (List<E>) getSession().createQuery(query);
	}

	public List<E> find(E entidade, String strStart, Map<String, Object> filter) {
		List<E> list = null;
		Map<String, Object> map = entidade.notEmptyFields(); 
		if( strStart != null ){
			if( map == null || map.isEmpty() ){
				list = startsWith((Class)entidade.getClass(), strStart );
			}else{
				list = startsWithAndFilter( (Class)entidade.getClass(), strStart, entidade.notEmptyFields() );
			}
		}else if( map != null && !entidade.notEmptyFields().isEmpty() ){
			list = findByFilter( (Class)entidade.getClass(), entidade.notEmptyFields() );
		}
		if( list.isEmpty() ){
			throw new ApplicationException( "Consulta sem resultados." );
		}
		return list;
	}
	
	private String montaQuery( Class<E> clazz, Map<String, Object> filter, String campoOrdenacao ){
		StringBuilder query = new StringBuilder();
		
		query.append("from ").append(clazz.getName()).append(" where 1=1 ");
		
		Set<String> setKey = filter.keySet();
		
		for( String key : setKey ){
			Object valor = filter.get( key );
			
			if( valor instanceof String || valor instanceof Character ){
				query.append(" and ( lower(")
					.append(key)
					.append(") like lower('%")
					.append(valor.toString())
					.append("%')) ");
			}
				
			else if( valor instanceof Date ){
				SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
				query.append(" and ")
					.append(key)
					.append(" = '")
					.append(df.format(valor)).append("' ");
			}else{
				query.append(" and ")
					.append(key)
					.append(" = ")
					.append(valor );
			}
		}
		
		if( campoOrdenacao != null )
			query.append(" order by ").append(campoOrdenacao);
		
		return query.toString();
	}

}
