package br.com.civitas.arquitetura.base.service;

import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.base.Usuario;
import br.com.civitas.arquitetura.base.entity.ControleSenha;
import br.com.civitas.arquitetura.persistence.AbstractPersistence;

@Service
public class ControleSenhaService extends AbstractPersistence<ControleSenha> {
	
	private static final long serialVersionUID = 8386347250010508756L;

	@SuppressWarnings("unchecked")
	public List<ControleSenha> buscarUltimosRegistros(Usuario usuario, Integer limite){
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT cs FROM ControleSenha cs ");
		sql.append(" WHERE cs.usuario = :usuario ");
		sql.append(" ORDER BY id DESC ");
		
		try{
			return getSessionFactory().getCurrentSession().createQuery(sql.toString())
					            .setParameter("usuario", usuario)
					            .setMaxResults(limite)
					            .list();
		} catch (NoResultException e){
			return null;
		}
	}

	@Override
	protected Class getClazz() {
		return ControleSenha.class;
	}
}