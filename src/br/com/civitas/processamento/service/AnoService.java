package br.com.civitas.processamento.service;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.Ano;

@Service
public class AnoService extends AbstractPersistence<Ano> {

	private static final long serialVersionUID = 955951648684367907L;

	@Override
	protected Class<Ano> getClazz() {
		return Ano.class;
	}
	
	@SuppressWarnings("unchecked")
	public List<Ano> buscarTodos() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT a FROM Ano a order by a.ano ");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		return (List<Ano>) query.list();
	}
	
}
