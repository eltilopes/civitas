package br.com.civitas.processamento.service;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.ArquivoPagamento;
import br.com.civitas.processamento.entity.ResumoSetor;

@Service
public class ResumoSetorService extends AbstractPersistence<ResumoSetor> {

	private static final long serialVersionUID = 670233112671915705L;

	@Override
	protected Class<ResumoSetor> getClazz() {
		return ResumoSetor.class;
	}

	@SuppressWarnings("unchecked")
	public List<ResumoSetor> buscarTodos() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT r FROM ResumoSetor r");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		return (List<ResumoSetor>) query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ResumoSetor> buscarPorArquivoPagamento(ArquivoPagamento arquivoPagamento) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT r FROM ResumoSetor r ");
		sql.append("LEFT JOIN FETCH  r.secretaria sec ");
		sql.append("LEFT JOIN FETCH  r.setor setor ");
		sql.append("LEFT JOIN FETCH  r.arquivoPagamento a ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND a = :arquivo ");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		query.setParameter("arquivo", arquivoPagamento);
		return (List<ResumoSetor>) query.list();
	}

}
