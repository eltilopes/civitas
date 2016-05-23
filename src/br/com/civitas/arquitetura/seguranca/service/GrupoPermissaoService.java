package br.com.civitas.arquitetura.seguranca.service;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.base.GrupoPermissao;
import br.com.civitas.arquitetura.persistence.AbstractPersistence;

@Service
public class GrupoPermissaoService extends AbstractPersistence<GrupoPermissao> {
	
	private static final long serialVersionUID = 7548723212580210934L;

	@Override
	protected  Class<GrupoPermissao>  getClazz() {
		return GrupoPermissao.class;
	}
	
	@SuppressWarnings("unchecked")
	public List<GrupoPermissao> buscarTodos() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT g FROM GrupoPermissao g");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		List <GrupoPermissao> grupos = (List<GrupoPermissao>) query.list();
		Collections.sort(grupos, (GrupoPermissao c1, GrupoPermissao c2) -> c1.getNome().compareTo(c2.getNome()));
		return grupos;
	}
	
}
