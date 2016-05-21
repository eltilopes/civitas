package br.com.civitas.arquitetura.seguranca.service;

import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.base.Perfil;
import br.com.civitas.arquitetura.persistence.AbstractPersistence;

@Service
public class PerfilService extends AbstractPersistence<Perfil> {
	
	private static final long serialVersionUID = -6710509157090982920L;

	@SuppressWarnings("unchecked")
	public List<Perfil> findByFilter(Map<String, Object> filter, Integer first, Integer rows) {
		
		String nome = (String) filter.get("nome");
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT per FROM Perfil per ");
		sql.append("LEFT JOIN FETCH per.permissoes ");
		sql.append("WHERE 1 = 1 ");
		
		if(StringUtils.isNotBlank(nome)){
			sql.append("AND UPPER(per.nome) LIKE UPPER(:nome) ");
		}
		
		sql.append("ORDER BY per.nome ASC ");
		
		Query query =  (Query) getSessionFactory().getCurrentSession().createQuery(sql.toString());
		
		if(StringUtils.isNotBlank(nome)){
			query.setParameter("nome", "%" + nome + "%");
		}
		
		if(first != null){
			query.setFirstResult(first);
		}
		
		if(rows != null){
			query.setMaxResults(rows);
		}

		return query.getResultList();
	}
	
	public Integer countByFilter(Map<String, Object> filter) {
		String nome = (String) filter.get("nome");

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(per) FROM Perfil per ");
		sql.append("WHERE 1 = 1 ");

		if(StringUtils.isNotBlank(nome)){
			sql.append("AND UPPER(per.nome) LIKE UPPER(:nome) ");
		}

		Query query =  (Query) getSessionFactory().getCurrentSession().createQuery(sql.toString());

		if(StringUtils.isNotBlank(nome)){
			query.setParameter("nome", "%" + nome + "%");
		}

		return ((Number)query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<Perfil> findAllAtivo() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT per FROM Perfil per ");
		sql.append("WHERE per.ativo = :ativo ");
		sql.append("ORDER BY per.nome ASC ");
		
		return   getSessionFactory().getCurrentSession().createQuery(sql.toString())
							 .setParameter("ativo", true)
							 .list();
	}

	@Override
	protected Class getClazz() {
		return Perfil.class;
	}

}
