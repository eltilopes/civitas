package br.com.civitas.processamento.service;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.Evento;

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

}
