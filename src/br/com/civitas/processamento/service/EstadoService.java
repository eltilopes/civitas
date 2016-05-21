package br.com.civitas.processamento.service;

import java.util.Collections;
import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.Estado;

@Service
public class EstadoService extends AbstractPersistence<Estado> {

	private static final long serialVersionUID = -4454160000438036883L;

	@Override
	protected Class<Estado> getClazz() {
		return Estado.class;
	}
	
	@SuppressWarnings("unchecked")
	public List<Estado> buscarTodos() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT e FROM Estado e ");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		List <Estado> estados = (List<Estado>) query.list();
		Collections.sort(estados, (Estado e1, Estado e2) -> e1.getDescricao().compareTo(e2.getDescricao()));
		return estados;
	}
	
	public Estado buscaPorCodigo(String uf){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT est ");
		sql.append("FROM Estado est ");
		sql.append("WHERE est.uf = :uf");
		Query query = (Query) getSessionFactory().getCurrentSession().createQuery(sql.toString())
									.setParameter("uf", uf);
		try{
			return (Estado) query.uniqueResult();
		} catch(NoResultException e){
			return null;
		}
	}

}
