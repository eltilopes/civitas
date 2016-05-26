package br.com.civitas.arquitetura.seguranca.service;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.base.Perfil;
import br.com.civitas.arquitetura.base.Permissao;
import br.com.civitas.arquitetura.persistence.AbstractPersistence;

@Service
public class PermissaoService extends AbstractPersistence<Permissao> {

	private static final long serialVersionUID = 8715007216223535281L;

	@Override
	protected Class<Permissao> getClazz() {
		return Permissao.class;
	}

	public List<Permissao> getListaPermissaoDisponiveis(Perfil perfil) {
		List<Permissao> permissoes = buscarTodos();
		permissoes.removeAll(perfil.getPermissoes());
		return permissoes;
	}
	
	@SuppressWarnings("unchecked")
	public List<Permissao> buscarTodos() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT p FROM Permissao p ");
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
		List <Permissao> permissoes = (List<Permissao>) query.list();
		Collections.sort(permissoes, (Permissao p1, Permissao p2) -> p1.getDescricao().compareTo(p2.getDescricao()));
		return permissoes;
	}
}
