package br.com.civitas.processamento.service;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.Vinculo;

@Service
public class VinculoService extends AbstractPersistence<Vinculo> {

	private static final long serialVersionUID = 7769581796753589911L;

	@Override
	protected Class<Vinculo> getClazz() {
		return Vinculo.class;
	}
	
	@SuppressWarnings("unchecked")
	public List<Vinculo> buscarTodos() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT v FROM Vinculo v");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		List <Vinculo> vinculos = (List<Vinculo>) query.list();
		Collections.sort(vinculos, (v1, v2) -> v1.getNumero().compareTo(v2.getNumero()));
		return vinculos;
	}

	public Vinculo buscarPorNumeroCidade(Vinculo vinculo) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT v FROM Vinculo v ");
		sql.append(" WHERE v.numero = :numero  ");
		sql.append(" AND v.cidade = :cidade  ");
		return (Vinculo) getSessionFactory().getCurrentSession().createQuery(sql.toString())
									  .setParameter("numero", vinculo.getNumero())
									  .setParameter("cidade", vinculo.getCidade())
									  .uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Vinculo> buscarPorCidade(Cidade cidade) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT v FROM Vinculo v ");
		sql.append(" WHERE  v.cidade = :cidade  ");
		return getSessionFactory().getCurrentSession().createQuery(sql.toString())
									  .setParameter("cidade", cidade)
									  .list();
	}
	
}
