package br.com.civitas.processamento.service;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.processamento.entity.Cidade;
import br.com.civitas.processamento.entity.Estado;

@Service
public class CidadeService extends AbstractPersistence<Cidade> {

	private static final long serialVersionUID = -7566389324690940805L;

	@Override
	protected Class<Cidade> getClazz() {
		return Cidade.class;
	}
	
	@SuppressWarnings("unchecked")
	public List<Cidade> buscarTodos() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT c FROM Cidade c");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		List <Cidade> cidades = (List<Cidade>) query.list();
		Collections.sort(cidades, (Cidade c1, Cidade c2) -> c1.getDescricao().compareTo(c2.getDescricao()));
		return cidades;
	}
	
	@SuppressWarnings("unchecked")
	public List<Cidade> buscarTodasAtivas() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT c FROM Cidade c WHERE c.ativa = :ativa ");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString()).setParameter("ativa", true);
		List <Cidade> cidades = (List<Cidade>) query.list();
		Collections.sort(cidades, (Cidade c1, Cidade c2) -> c1.getDescricao().compareTo(c2.getDescricao()));
		return cidades;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Cidade> findByEstado(Estado estado){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT cid FROM Cidade cid WHERE cid.estado = :estado ");
		return getSessionFactory().getCurrentSession().createQuery(sql.toString()).setParameter("estado", estado).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Cidade> findByNameMaisEstado(String nome, Estado estado){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT cid FROM Cidade cid WHERE UPPER(cid.descricao) = UPPER(:cidade) AND cid.estado = :estado ");
		return getSessionFactory().getCurrentSession().createQuery(sql.toString()).setParameter("cidade", nome).setParameter("estado", estado).list();
	}

	@SuppressWarnings("unchecked")
	public List<Cidade> buscarCidadesComArquivosProcessados() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT DISTINCT cid FROM ArquivoPagamento ap ");
		sql.append(" INNER JOIN ap.cidade cid ");
		sql.append(" ORDER BY cid.descricao ASC ");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		return query.list();
	}	
}
