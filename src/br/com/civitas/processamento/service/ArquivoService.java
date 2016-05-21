package br.com.civitas.processamento.service;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.Arquivo;

@Service
public class ArquivoService extends AbstractPersistence<Arquivo> {

	private static final long serialVersionUID = -7300710483142299659L;

	@Override
	protected Class<Arquivo> getClazz() {
		return Arquivo.class;
	}

	@SuppressWarnings("unchecked")
	public Collection<Arquivo> buscarTodos() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT a FROM Arquivo a");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		return (List<Arquivo>) query.list();
	}

	public Arquivo buscarPorArquivo(Arquivo arquivo) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT a FROM Arquivo a ");
		sql.append(" WHERE a.ano = :ano ");
		sql.append(" AND a.mes = :mes  ");
		sql.append(" AND a.cidade = :cidade  ");
		sql.append(" AND a.tipoArquivo = :tipoArquivo  ");
		return (Arquivo) getSessionFactory().getCurrentSession().createQuery(sql.toString())
									  .setParameter("ano", arquivo.getAno())
									  .setParameter("mes", arquivo.getMes())
									  .setParameter("cidade", arquivo.getCidade())
									  .setParameter("tipoArquivo", arquivo.getTipoArquivo())
									  .uniqueResult();
	}
	
}
