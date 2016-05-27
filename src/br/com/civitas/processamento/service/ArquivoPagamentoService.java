package br.com.civitas.processamento.service;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.ArquivoPagamento;

@Service
public class ArquivoPagamentoService extends AbstractPersistence<ArquivoPagamento> {

	private static final long serialVersionUID = -7300710483142299659L;

	@Override
	protected Class<ArquivoPagamento> getClazz() {
		return ArquivoPagamento.class;
	}

	@SuppressWarnings("unchecked")
	public Collection<ArquivoPagamento> buscarTodos() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT a FROM ArquivoPagamento a");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		return (List<ArquivoPagamento>) query.list();
	}

	public ArquivoPagamento buscarPorArquivo(ArquivoPagamento arquivo) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT a FROM ArquivoPagamento a ");
		sql.append(" WHERE a.ano = :ano ");
		sql.append(" AND a.mes = :mes  ");
		sql.append(" AND a.cidade = :cidade  ");
		sql.append(" AND a.tipoArquivo = :tipoArquivo  ");
		return (ArquivoPagamento) getSessionFactory().getCurrentSession().createQuery(sql.toString())
									  .setParameter("ano", arquivo.getAno())
									  .setParameter("mes", arquivo.getMes())
									  .setParameter("cidade", arquivo.getCidade())
									  .setParameter("tipoArquivo", arquivo.getTipoArquivo())
									  .uniqueResult();
	}
	
}
