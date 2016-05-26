package br.com.civitas.arquitetura.base.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.NoResultException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.arquitetura.base.Permissao;
import br.com.civitas.arquitetura.base.Usuario;
import br.com.civitas.arquitetura.base.entity.ControleBloqueioUsuario;
import br.com.civitas.arquitetura.base.vo.EmailVO;
import br.com.civitas.arquitetura.persistence.AbstractPersistence;
import br.com.civitas.arquitetura.util.Digest;
import br.com.civitas.arquitetura.util.EmailUtil;
import br.com.civitas.arquitetura.util.ResourceUtils;

@Service
public class UsuarioService extends AbstractPersistence<Usuario>{
	
	private static final long serialVersionUID = 3498593952944611981L;

	@Autowired EmailUtil emailUtil;
	@Autowired ResourceUtils resourcesUtils;
	
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
		Query query = getSessionFactory().getCurrentSession().createQuery(sql.toString());
	    query.setParameter(LOGIN, usuario.getLogin());
		return !query.list().isEmpty();
	}
	
	public boolean validarSenha(Usuario usuario, String senha){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT usu from Usuario usu WHERE UPPER(usu.login) LIKE UPPER(:login) AND senha = :senha ");
		Query query =  getSessionFactory().getCurrentSession().createQuery(sql.toString());
		query.setParameter(LOGIN, usuario.getLogin());
		query.setParameter(SENHA, senha);
		return !query.list().isEmpty();
	}
	
	@Transactional
	public boolean alterarSenha(Usuario usuario, String senha, Boolean alteraSenha){
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE FROM Usuario SET alteraSenha = :alteraSenha, senha = :senha WHERE id = :id ");
		Query query =  getSessionFactory().getCurrentSession().createQuery(sql.toString());
		query.setParameter(ALTERA_SENHA, alteraSenha);
		query.setParameter(SENHA, senha);
		query.setParameter(ID, usuario.getId());
		return query.executeUpdate() >= 1 ? true : false;
	}
	
	@Transactional
	public boolean updateAtivo(Usuario usuario, Boolean value){
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE FROM Usuario SET ativo = :ativo WHERE id = :id");
		Query query =  getSessionFactory().getCurrentSession().createQuery(sql.toString());
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
	
	@Transactional( propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class} )
	public void reiniciarSenha( Usuario usuario ){
		usuario.setLogin( usuario.getLogin() != null ? usuario.getLogin().trim() : null );
		usuario.setEmail( usuario.getEmail() != null ? usuario.getEmail().trim() : null );
		List<Usuario> l = findByFilter( Usuario.class, usuario.notEmptyFields() );
		
		if(l.isEmpty()){
			throw new ApplicationException( "Os dados informados não conferem!" );
		} 
		
		usuario = l.get(0);
		String senha = EmailUtil.geraSenha();
		usuario.setSenha( Digest.MD5digest(senha) );
		
		atualizarSenhaToLogin(usuario);

		// envio do e-mail
		EmailVO email = new EmailVO();
		email.setDestino( usuario.getEmail() );
		email.setRemetente( "CÍVITAS  <administrador@civitas.com.br>" );
		email.setAssunto( "CÍVITAS - Senha de Acesso" );
		email.setMensagem( this.geraMensagemRedefinirSenha( usuario.getLogin(), senha ) );
		
		getEmailUtil().enviar( email );
	}
	
	public String geraMensagemRedefinirSenha( String usuario, String senha ) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("usuario", usuario);
		params.put("senha",senha);
		return getResourcesUtils().getResource("redefinir-senha.html", params);
	}
	
	@SuppressWarnings("unchecked")
	public List<Permissao> findPermissaoByUsuario(Usuario usuario) {
		StringBuilder jpQL = new StringBuilder();
		jpQL.append("SELECT perm FROM Perfil per ");
		jpQL.append("INNER JOIN per.permissoes perm ");
		jpQL.append("INNER JOIN per.usuarios usu ");
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
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE FROM Usuario u SET u.senha = :senha WHERE u.id = :id");
		Query query =  getSessionFactory().getCurrentSession().createQuery(sql.toString());
		query.setParameter(SENHA, Digest.MD5digest(usuario.getSenha()));
		query.setParameter(ID, usuario.getId());
		query.executeUpdate();
	}	
	
	@Override
	protected Class<Usuario> getClazz() {
		return Usuario.class;
	}

	public EmailUtil getEmailUtil() {
		return emailUtil;
	}

	public void setEmailUtil(EmailUtil emailUtil) {
		this.emailUtil = emailUtil;
	}

	public ResourceUtils getResourcesUtils() {
		return resourcesUtils;
	}

	public void setResourcesUtils(ResourceUtils resourcesUtils) {
		this.resourcesUtils = resourcesUtils;
	}
	
	
}