package br.com.civitas.processamento.service;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.Mes;

@Service
public class MesService extends AbstractPersistence<Mes> {

	private static final long serialVersionUID = 4908502639333505295L;

	@Override
	protected Class<Mes> getClazz() {
		return Mes.class;
	}
	
	@SuppressWarnings("unchecked")
	public List<Mes> buscarTodos() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT m FROM Mes m");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		List <Mes> meses = (List<Mes>) query.list();
		Collections.sort(meses, (m1, m2) -> m1.getNumero().compareTo(m2.getNumero()));
		return meses;
	}
	
}
