package br.com.civitas.processamento.service;

import java.util.Collection;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.Evento;
import br.com.civitas.processamento.enums.TipoArquivo;

@Service
public class EventoService extends AbstractPersistence<Evento> {

	private static final long serialVersionUID = 5814955646678856301L;

	@Override
	protected Class<Evento> getClazz() {
		return Evento.class;
	}

	@SuppressWarnings("unchecked")
	public List<Evento> buscarTodos() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT e FROM Evento e");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		return (List<Evento>) query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Evento> buscarPorChave(Collection<String> chaves) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT e FROM Evento e ");
		sql.append("WHERE UPPER(e.chave) IN (:chaves) " );
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString()).setParameterList("chaves", chaves);
		return (List<Evento>) query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Evento> buscarTipoArquivoCidade(Cidade cidade, TipoArquivo tipoArquivo) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT DISTINCT e FROM Evento e ");
		sql.append(" WHERE e.cidade = :cidade  ");
		sql.append(" AND e.tipoArquivo = :tipoArquivo  ");
		
		return  getSessionFactory().getCurrentSession().createQuery(sql.toString())
									  .setParameter("cidade", cidade)
									  .setParameter("tipoArquivo", tipoArquivo)
									  .list();
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<Evento> buscarCidade(Cidade cidade) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select e.nm_nome as nome, e.nm_chave as chave, cast(e.ci_evento as bigint) as id  from tb_evento e ");
		sql.append(" left join tb_evento_pagamento ep ON ep.cd_evento = e.ci_evento ");
		sql.append(" where e.cd_cidade = :cidade ");
		sql.append(" group by e.nm_nome, e.ci_evento,  e.nm_chave ");
		sql.append(" order by count(ep.ci_evento_pagamento ) desc ");
		
		Session session = (Session) getSessionFactory().getCurrentSession();
		org.hibernate.Query query = session.createSQLQuery(sql.toString())
				.addScalar("id", Hibernate.LONG)
				.addScalar("nome")
				.addScalar("chave");
		query.setResultTransformer(Transformers.aliasToBean(Evento.class));
		return  query.setParameter("cidade", cidade.getId()).list();
	}

}
