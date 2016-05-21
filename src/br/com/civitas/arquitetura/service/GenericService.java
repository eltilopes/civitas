package br.com.civitas.arquitetura.service;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.arquitetura.converter.DefaultConverter;
import br.com.civitas.arquitetura.dao.GenericDao;
import br.com.civitas.arquitetura.entity.Entidade;

/**
 * Serviço genérico que implementa os métodos do ServiceCrud
 * @author Samuel Soares
 *
 * @param <E> Entidade gerenciada por esse service
 */
@org.springframework.stereotype.Service("genericService")
@HandlerException
public class GenericService<E extends Entidade> implements ServiceCrud<E>{

	private static final long serialVersionUID = 645783535761106635L;
	
	/** Dao utilizado para acesso ao banco de dados. */
	@Resource(name = "dao")
	protected GenericDao<E> dao;
	
	// @Override
	public List<E> findAll( Class<E> clazz ) {
		return dao.findAll(clazz);
	}

	@Override
	public List<E> findAll( Class<E> clazz, String campoOrdenacao ) {
		return dao.findAll( clazz, campoOrdenacao );
	}
	
	@Override
	public List<E> findByExample(E obj) {
		return dao.findByExample(obj);
	}

	@Override
	public E findByPrimaryKey(Class<E> clazz, Serializable id) {
		return (E)dao.findByPrimaryKey(clazz, id);
	}

	@Override
	@Transactional( propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class )
	@HandlerException(value = DataIntegrityViolationException.class, message = "Já existe um registro com o(s) mesmo(s) parâmetro(s) informado(s).")
	public E insert(E obj) {
		return dao.insert(obj);
	}

	@Override
	@Transactional( propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class )
	public void insertAll(Collection<E> collection) {
		dao.insertAll(collection);		
	}

	@Override
	@Transactional( propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class )
	@HandlerException(value = DataIntegrityViolationException.class, message = "O registro não pode ser removido. Verifique se existem itens dependentes desse registro.[2x]")
	public void remove(E obj) {
		try{
			dao.remove(obj);
		}catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException("O registro não pode ser removido. Verifique se existem itens dependentes desse registro.");
		}
	}
	
	@Override
	public void removeAll(Collection<E> collection) {
		
		StringBuilder mensagem = new StringBuilder("Os itens de IDs ");
		List<Integer> lista = new ArrayList<Integer>();
		List<E> listaRetorno = new ArrayList<E>();
		Integer numero;
		boolean erro = false;
		
		for (E e : collection) {
			try {
				dao.remove(e);
			} catch (DataIntegrityViolationException ape) {
				erro = true;
				DefaultConverter d = new DefaultConverter();
				String id = d.getId(e);
				numero = new Integer(id);
				lista.add(numero);
				listaRetorno.add(e);
			}
		}
		if(erro){
			
			int i = 1; 
			int totalLista = lista.size();
			for (Integer in : lista) {
				if(i == totalLista){
					mensagem.append(in);
				}else{
					mensagem.append(in);
					mensagem.append(", ");
				}
				i++;
			}
			mensagem.append(" não podem ser excluídos. Verifique se existem transações ou outros itens dependentes desse grupo.");
			collection.clear();
			collection.addAll(listaRetorno);
			throw new ApplicationException(mensagem.toString());
		}
		
	}

	@Override
	@Transactional( propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class )
	public E update(E obj) {
		return dao.update(obj);
	}

	@Override
	@Transactional( propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class )
	public void updateAll(Collection<E> collection) {
		dao.updateAll(collection);
	}

	@Override
	public List<E> startsWith( Class<E> clazz, String strStart ){
		return dao.startsWith(clazz, strStart);
	}

	@Override
	public List<E> startsWithAndFilter( Class<E> clazz, String strStart, Map<String, Object> filter ){
		return dao.startsWithAndFilter(clazz, strStart, filter);
	}
	
	@Override
	public List<E> findByFilter(Class<E> clazz, Map<String, Object> filter) {
		return dao.findByFilter(clazz, filter);
	}
	
	@Override
	public List<E> findByFilter(Class<E> clazz, Map<String, Object> filter, String campoOrdenacao) {
		return dao.findByFilter(clazz, filter, campoOrdenacao);
	}
	
	@Override @SuppressWarnings({ "unchecked", "rawtypes" })
	public List<E> find(E entidade, String strStart, Map<String, Object> filter ){
		List<E> list = null;
		Map<String, Object> map = entidade.notEmptyFields(); 
		if( strStart != null ){
			if( map == null || map.isEmpty() ){
				list = dao.startsWith((Class)entidade.getClass(), strStart );
			}else{
				list = dao.startsWithAndFilter( (Class)entidade.getClass(), strStart, entidade.notEmptyFields() );
			}
		}else if( map != null && !entidade.notEmptyFields().isEmpty() ){
			list = dao.findByFilter( (Class)entidade.getClass(), entidade.notEmptyFields() );
		}
		if( list.isEmpty() ){
			throw new ApplicationException( "Itens não encontrados." );
		}
		return list;
	}

	/**
	 * Dao genérico utilizado no acesso ao banco de dados.
	 * @return
	 */
	public GenericDao<E> getDao() {
		return dao;
	}
	
	public void setDao(GenericDao<E> dao) {
		this.dao = dao;
	}
	
	/**
	 * @return {@link HibernateTemplate} 
	 */
	public HibernateTemplate getEntityManager(){
		return this.getDao().getEntityManager();
	}
	
	/**
	 * @return {@link DataSource}
	 */
	public DataSource getDataSource(){
		return this.getDao().getDataSource();
	}
	
}