package br.com.civitas.arquitetura.base.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.civitas.arquitetura.base.Permissao;
import br.com.civitas.arquitetura.base.Usuario;
import br.com.civitas.arquitetura.base.entity.ControleBloqueioUsuario;
import br.com.civitas.arquitetura.persistence.AbstractPersistence;

@Service
public class UsuarioService extends AbstractPersistence<Usuario>{
	
	private static final long serialVersionUID = 3664773229681248350L;
	
	public static final String ID = "id";
	public static final String NOME = "nome";
	public static final String NULL = "null";
	public static final String LOGIN = "login";
	public static final String SENHA = "senha";
	public static final String ALTERA_SENHA = "alteraSenha";
	public static final String ATIVO = "ativo";
	public static final String USUARIO = "usuario";
	
	public Usuario buscarPorUsuario(Usuario usuario){
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT usu FROM Usuario usu ");
		sql.append(" INNER JOIN usu.grupoEmpresa gp ");
		sql.append(" LEFT JOIN FETCH usu.perfis per ");
		sql.append(" WHERE usu = :usuario  ");
		sql.append(" ORDER BY gp.descricao ");
		
		return (Usuario) getSessionFactory().getCurrentSession().createQuery(sql.toString())
									  .setParameter(USUARIO, usuario)
									  .uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Usuario> findByFilter(Map<String, Object> filter, Integer first, Integer rows) {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT usu FROM Usuario usu ");
		sql.append(" INNER JOIN usu.grupoEmpresa gp ");
		sql.append(" LEFT JOIN FETCH usu.perfis per ");
		sql = getRestricao(sql, filter);
		sql.append(" ORDER BY gp.descricao ");
		return getQuery(sql, filter, first, rows).getResultList();
	}
	
	public Integer countByFilter(Map<String, Object> filter) {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT COUNT(usu) FROM Usuario usu ");
		sql.append(" INNER JOIN usu.grupoEmpresa gp ");
		sql = getRestricao(sql, filter);
		return ((Number)getQuery(sql, filter, null, null).getSingleResult()).intValue();
	}
	
	private StringBuilder getRestricao(StringBuilder sql, Map<String, Object> filter){
		
		
		Object ativo = filter.get(ATIVO);
		String login = (String) filter.get(LOGIN);
		String nome = (String) filter.get(NOME);
	
		sql.append(" WHERE 1 = 1 ");
		sql.append(!ativo.equals(NULL) ? " AND usu.ativo = :ativo " : "");
		sql.append(StringUtils.isNotBlank(login) ? " AND UPPER(usu.login) like UPPER(:login) " : "");
		sql.append(StringUtils.isNotBlank(nome) ? " AND UPPER(usu.nome) like UPPER(:nome) " : "");
		return sql;
	}
	
	private Query getQuery(StringBuilder sql, Map<String, Object> filter, Integer first, Integer rows){
		
		Object ativo = filter.get(ATIVO);
		String login = (String) filter.get(LOGIN);
		String nome = (String) filter.get(NOME);
		
		Query query = (Query) getSessionFactory().getCurrentSession().createQuery(sql.toString());
		
		if(!ativo.equals(NULL)) {
			query.setParameter(ATIVO, Boolean.parseBoolean(ativo.toString()));
		}
		
		if(StringUtils.isNotBlank(login)) {
			query.setParameter(LOGIN, "%" + login + "%");
		}
		
		if(StringUtils.isNotBlank(nome)) {
			query.setParameter(NOME, "%" + nome + "%");
		}

		if(first != null){
			query.setFirstResult(first);
		}
		
		if(rows != null){
			query.setMaxResults(rows);
		}
		
		return query;
		
	}

	public Usuario findByLoginAtivo(String login) {
		try{
			return (Usuario) getSessionFactory().getCurrentSession().createQuery("SELECT usu FROM Usuario usu WHERE usu.login = :login AND usu.ativo = :ativo")
										  .setParameter(LOGIN, login)
										  .setParameter(ATIVO, Boolean.TRUE)
										  .uniqueResult();
		}catch (NoResultException e) {
			return null;
		}
	}
	
	public Usuario findByLogin(Usuario usuarioLogin) {
		Usuario usuario;
		try{
			usuario =(Usuario) getSessionFactory().getCurrentSession().createQuery("SELECT usu FROM Usuario usu WHERE usu.login = :login AND usu.ativo = :ativo")
					.setParameter(LOGIN, usuarioLogin.getLogin())
					.setParameter(ATIVO, Boolean.TRUE)
					.uniqueResult();
		}catch (NoResultException e) {
			return null;
		}
		if(Objects.nonNull(usuario) && Objects.nonNull(usuario.getId())){
			usuarioLogin.setId(usuario.getId());
			usuarioLogin.setRoot(usuario.getRoot());
		}
		return usuarioLogin;
	}
	
	public boolean isValidLogin(Usuario usuario){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT usu from Usuario usu WHERE UPPER(usu.login) LIKE UPPER(:login) ");
		Query query = (Query) getSessionFactory().getCurrentSession().createQuery(sql.toString());
	    query.setParameter(LOGIN, usuario.getLogin());
		return !query.getResultList().isEmpty();
	}
	
	public boolean validarSenha(Usuario usuario, String senha){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT usu from Usuario usu WHERE UPPER(usu.login) LIKE UPPER(:login) AND senha = :senha ");
		Query query = (Query) getSessionFactory().getCurrentSession().createQuery(sql.toString());
		query.setParameter(LOGIN, usuario.getLogin());
		query.setParameter(SENHA, senha);
		return !query.getResultList().isEmpty();
	}
	
	@Transactional
	public boolean alterarSenha(Usuario usuario, String senha, Boolean alteraSenha){
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE FROM Usuario SET alteraSenha = :alteraSenha, senha = :senha WHERE id = :id ");
		Query query = (Query) getSessionFactory().getCurrentSession().createQuery(sql.toString());
		query.setParameter(ALTERA_SENHA, alteraSenha);
		query.setParameter(SENHA, senha);
		query.setParameter(ID, usuario.getId());
		return query.executeUpdate() >= 1 ? true : false;
	}
	
	@Transactional
	public boolean updateAtivo(Usuario usuario, Boolean value){
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE FROM Usuario SET ativo = :ativo WHERE id = :id");
		Query query = (Query) getSessionFactory().getCurrentSession().createQuery(sql.toString());
		query.setParameter(ATIVO, value ? true : false);
		query.setParameter(ID, usuario.getId());
		return query.executeUpdate() >= 1 ? true : false;
	}
	
	@Transactional
	public Usuario save(Usuario usuario) {
		if(!(usuario.getId() == null) && StringUtils.isBlank(usuario.getSenha())){
			Usuario usuarioAnterior = findByID(usuario);
			usuario.setSenha(usuarioAnterior.getSenha());
		}
		super.insert(usuario);
		
		return usuario;
	}
	
	@SuppressWarnings("unchecked")
	public List<Permissao> findPermissaoByUsuario(Usuario usuario) {
		StringBuilder jpQL = new StringBuilder();
		jpQL.append("SELECT perm FROM Perfil per ");
		jpQL.append("INNER JOIN per.permissoes perm ");
		jpQL.append("INNER JOIN per.usuarios usu ");
		jpQL.append("INNER JOIN usu.grupoEmpresa ge ");
		jpQL.append("WHERE usu = :usuario");
		return  getSessionFactory().getCurrentSession().createQuery(jpQL.toString()).setParameter(USUARIO, usuario)  .list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Permissao> findAllPermissao() {
		return  getSessionFactory().getCurrentSession().createQuery("SELECT per FROM Permissao per ORDER BY id DESC").list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Usuario> buscarUsuarioAtivo(){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT usu FROM Usuario usu ");
		sql.append(" WHERE ativo = true  ");
		return getSessionFactory().getCurrentSession().createQuery(sql.toString()).list();
	}
	
	public ControleBloqueioUsuario buscarUltimoBloqueio(Usuario usuario){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT controle FROM ControleBloqueioUsuario controle ");
		sql.append(" WHERE usuario = :usuario ");
		sql.append(" ORDER BY id DESC ");
		try{
			return (ControleBloqueioUsuario) getSessionFactory().getCurrentSession().createQuery(sql.toString())
								.setMaxResults(1)
								.setParameter("usuario", usuario)
								.uniqueResult();
		} catch (NoResultException e){
			return null;
		}
	}

	public Usuario findByID(Usuario usuario) {
		return (Usuario) getSessionFactory().getCurrentSession().get(Usuario.class, usuario.getId());
	}

	@Transactional( propagation = Propagation.REQUIRED, rollbackFor = Exception.class )
	public void atualizarSenhaToLogin(Usuario usuario) {
		String senha = usuario.getSenha();
//		getSessionFactory().getCurrentSession().create("update Usuario set senha = ? where id = ?", 
//				br.com.civitas.helpers.utils.StringUtils.md5(senha), usuario.getId());
	}	
	@Override
	protected Class getClazz() {
		return null;
	}
	
}