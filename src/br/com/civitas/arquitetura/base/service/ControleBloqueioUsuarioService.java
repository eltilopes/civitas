package br.com.civitas.arquitetura.base.service;

import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.base.Usuario;
import br.com.civitas.arquitetura.base.entity.ControleBloqueioUsuario;
import br.com.civitas.arquitetura.persistence.AbstractPersistence;

@Service
public class ControleBloqueioUsuarioService extends AbstractPersistence<ControleBloqueioUsuario>{
	
	private static final long serialVersionUID = 5526419949473677319L;

	public ControleBloqueioUsuario buscarControleBloqueioUsuario(Usuario usuario){
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT cbu FROM ControleBloqueioUsuario cbu ");
		sql.append(" WHERE cbu.usuario = :usuario ");
		sql.append(" ORDER BY id DESC ");
		
		try{
			return (ControleBloqueioUsuario) getSessionFactory().getCurrentSession().createQuery(sql.toString())
														  .setParameter("usuario", usuario)
														  .setMaxResults(1)
														  .uniqueResult();
		} catch (NoResultException e){
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ControleBloqueioUsuario> buscarPorUsuario(Usuario usuario){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT cbu FROM ControleBloqueioUsuario cbu ");
		sql.append(" WHERE cbu.usuario = :usuario ");
		sql.append(" ORDER BY id DESC ");
		return getSessionFactory().getCurrentSession().createQuery(sql.toString())
							.setParameter("usuario", usuario)
							.setMaxResults(10)
							.list();
	}

	@Override
	protected Class getClazz() {
		return ControleBloqueioUsuario.class;
	}
}