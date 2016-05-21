package br.com.civitas.processamento.service;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.Matricula;

@Service
public class MatriculaService extends AbstractPersistence<Matricula> {

	private static final long serialVersionUID = -5829554267277214171L;

	@Override
	protected Class<Matricula> getClazz() {
		return Matricula.class;
	}

	@SuppressWarnings("unchecked")
	public List<Matricula> buscarTodos() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT m FROM Matricula m");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		return (List<Matricula>) query.list();
	}
	
	public Matricula salvar(Matricula matricula){
		getSession().merge(matricula.getVinculo());
		getSession().merge(matricula.getCargo());
		return save(matricula);
	}

}
