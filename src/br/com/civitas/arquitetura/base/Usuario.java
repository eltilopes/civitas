package br.com.civitas.arquitetura.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.constraints.Email;
import org.joda.time.DateTime;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.civitas.arquitetura.entity.IEntity;
import br.com.civitas.arquitetura.util.JSFUtil;

@Entity
@Table(name = "tb_usuario")
public class Usuario implements IEntity,UserDetails {
	
	private static final long serialVersionUID = -8932553654853495055L;
	
	@Id
	@GeneratedValue(generator = "seq_usuario", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_usuario", sequenceName="seq_usuario", allocationSize=1)
	@Column(name = "ci_usuario")
	private Long id;
	
	@Column(name="nm_nome",nullable = false)
	private String nome;

	@Column(name="fl_ativo",nullable = false)
	private Boolean ativo;

	@Column(name="ds_login",unique = true,nullable = false)
	private String login;

	@Column(name="ds_senha",nullable = false)
	private String senha;

	@Column(name = "fl_altera_senha",nullable = false)
	private Boolean alteraSenha;

	@ManyToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "tb_usuario_perfil",
		joinColumns = @JoinColumn(name = "cd_usuario", referencedColumnName = "ci_usuario") , 
		inverseJoinColumns = @JoinColumn(name = "cd_perfil", referencedColumnName = "ci_perfil") )
	private List<Perfil> perfis;
	
	@Column(name="fl_root",nullable = false)
	private Boolean root;
	
	@Email(message="Email invalido")
	@Column(name="ds_email",nullable = false)
	private String email;

	@Transient
	private Collection<GrantedAuthority> authorities;

	@Transient
	private List<LogAcesso> logAcessos;
	
	@Transient
	private String senhaEmail;
	
	public Usuario() {
		super();
		this.ativo = false;
		this.alteraSenha = true;
		this.root = false;
		this.perfis = new ArrayList<Perfil>();
		this.logAcessos = new ArrayList<LogAcesso>();
	}
	
	public Usuario(String login) {
		super();
		this.login = login;
		this.root = false;
	}
	
	public Usuario(Long id) {
		super();
		this.alteraSenha = true;
		this.ativo = true;
		this.root = false;
		this.perfis = new ArrayList<Perfil>();
		this.logAcessos = new ArrayList<LogAcesso>();
		setId(id);
	}
	
	public Usuario(Long id, String login, String senha) {
		super();
		this.alteraSenha = true;
		this.ativo = true;
		this.root = false;
		this.perfis = new ArrayList<Perfil>();
		this.logAcessos = new ArrayList<LogAcesso>();
		setId(id);
		this.login = login;
		this.senha = senha;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome.toUpperCase();
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login.toUpperCase();
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Boolean getAlteraSenha() {
		return alteraSenha;
	}

	public void setAlteraSenha(Boolean alteraSenha) {
		this.alteraSenha = alteraSenha;
	}

	public boolean hasPermission(String chave) {
		for (GrantedAuthority grantedAuthority : getAuthorities()) {
			if (grantedAuthority.getAuthority().equals(chave)) {
				return true;
			}
		}
		return false;
	}
	
	public void setPermissoes(List<Permissao> listPermissaoUsuario) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		
		for (Permissao permissao : listPermissaoUsuario) {
            grantedAuthorities.add(new SimpleGrantedAuthority(permissao.getChave()));
        }
        
        this.setAuthorities(grantedAuthorities);
		
	}

	public Collection<GrantedAuthority> getAuthorities() {
		if (this.authorities == null) {
			this.authorities = new ArrayList<GrantedAuthority>();
		}
		return this.authorities;
	}

	public void setAuthorities(Collection<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public String getPassword() {
		return this.senha;
	}

	public String getUsername() {
		return this.login;
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	public boolean isEnabled() {
		return getAtivo();
	}

	public List<Perfil> getPerfis() {
		return perfis;
	}

	public void setPerfis(List<Perfil> perfis) {
		this.perfis = perfis;
	}

	public Boolean getRoot() {
		return root;
	}

	public void setRoot(Boolean root) {
		this.root = root;
	}

	public List<LogAcesso> getLogAcessos() {
		return logAcessos;
	}

	public void setLogAcessos(List<LogAcesso> logAcessos) {
		this.logAcessos = logAcessos;
	}

	public String getEmail() {
		if(email == null){
			return email;
		}
		return email.toLowerCase();
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenhaEmail() {
		return senhaEmail;
	}

	public void setSenhaEmail(String senhaEmail) {
		this.senhaEmail = senhaEmail;
	}
	
	public String getUltimoAcesso(){
		if(!logAcessos.isEmpty()){
			Collections.sort(logAcessos);
			LogAcesso logAcesso = logAcessos.get(logAcessos.size() - 1);
			return JSFUtil.getMessageFromBundle("lbl_ultimo_acesso") + logAcesso.getData().toString();
		}
		return null;
	}
	
	public String getDiaDaSemana(){
		if(!logAcessos.isEmpty()){
			Collections.sort(logAcessos);
			LogAcesso logAcesso = logAcessos.get(logAcessos.size() - 1);
			return new DateTime(logAcesso.getDataAcesso()).dayOfWeek().getAsText();
		}
		return null;
	}
	
	public String getHoraAcesso(){
		if(!logAcessos.isEmpty()){
			Collections.sort(logAcessos);
			LogAcesso logAcesso = logAcessos.get(logAcessos.size() - 1);
			return logAcesso.getHora();
		}
		return null;
	}
	
	public Boolean getNaoRenderizarLogAcesso(){
		return getUltimoAcesso() == null || getDiaDaSemana() == null || getHoraAcesso() == null;
	}


	@Override
	public Map<String, Object> notEmptyFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		if( nome != null && !nome.trim().isEmpty() )
			map.put("nome", nome);
		if( login != null && !login.trim().isEmpty() )
			map.put("login", login);
		if( email != null && !email.trim().isEmpty() )
			map.put("email", email);
		return map;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}