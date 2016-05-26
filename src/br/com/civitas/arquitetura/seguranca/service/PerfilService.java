package br.com.civitas.arquitetura.seguranca.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.civitas.arquitetura.base.Perfil;
import br.com.civitas.arquitetura.persistence.AbstractPersistence;

@Service
public class PerfilService extends AbstractPersistence<Perfil> {
	
	private static final long serialVersionUID = -6710509157090982920L;


	@SuppressWarnings("unchecked")
	public List<Perfil> buscarTodosAtivos() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT per FROM Perfil per ");
		sql.append("WHERE per.ativo = :ativo ");
		sql.append("ORDER BY per.nome ASC ");
		
		return   getSessionFactory().getCurrentSession().createQuery(sql.toString())
							 .setParameter("ativo", true)
							 .list();
	}

	@Override
	protected Class<Perfil> getClazz() {
		return Perfil.class;
	}

}
