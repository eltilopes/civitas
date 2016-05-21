package br.com.civitas.acesso;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import br.com.civitas.arquitetura.base.Usuario;
import br.com.civitas.arquitetura.base.entity.ControleBloqueioUsuario;
import br.com.civitas.arquitetura.base.service.ControleBloqueioUsuarioService;
import br.com.civitas.arquitetura.base.service.ControleSenhaService;
import br.com.civitas.arquitetura.base.service.UsuarioService;
import br.com.civitas.arquitetura.service.AuthenticationService;
import br.com.civitas.arquitetura.util.FacesUtils;
import br.com.civitas.arquitetura.util.JSFUtil;
import br.com.civitas.arquitetura.util.SystemException;

@SessionScoped
@ManagedBean
public class LoginCivitasBean implements Serializable {

	private static final long serialVersionUID = 2953115597277505533L;

	private Usuario usuario;
	
	@ManagedProperty(value = "#{usuarioService}")
	private UsuarioService service;
	
	@ManagedProperty(value = "#{authenticationService}")
	private AuthenticationService authenticationService;
	
	@ManagedProperty(value="#{controleSenhaService}")
	private ControleSenhaService controleSenhaService;
	
	@ManagedProperty(value="#{controleBloqueioUsuarioService}")
	private ControleBloqueioUsuarioService controleBloqueioUsuarioService;

	public String logar() throws SystemException {
		usuario.setLogin(usuario.getLogin().toUpperCase());
		if(!validCampos()){
			JSFUtil.addGlobalMessage("todos_campos_obrigatorios", FacesMessage.SEVERITY_WARN);
			return null;
		}
		usuario = service.findByLogin(usuario);
		if(Objects.isNull(usuario.getId())){
			JSFUtil.addGlobalMessage("usuario_ou_senha_errado", FacesMessage.SEVERITY_WARN);
			return null;
		}
		ControleBloqueioUsuario controleBloqueioUsuario = null;
		if(!usuario.getRoot()){
			controleBloqueioUsuario = controleBloqueioUsuarioService.buscarControleBloqueioUsuario(usuario);
		}
		if(controleBloqueioUsuario != null && !controleBloqueioUsuario.getLoginSucesso()){
			if(controleBloqueioUsuario.getTentativa() == 6 && controleBloqueioUsuario.getBloqueado()){
				Integer minutos = Minutes.minutesBetween(new DateTime(controleBloqueioUsuario.getDataBloqueio()), new DateTime()).getMinutes();
				if(minutos >= 30){
					if(!logaUser()){
						controleBloqueioUsuario.setBloqueado(Boolean.FALSE);
						controleBloqueioUsuarioService.save(controleBloqueioUsuario);
						controleBloqueioUsuarioService.save(new ControleBloqueioUsuario(usuario));
						return null;
					}
					controleBloqueioUsuario.setBloqueado(Boolean.FALSE);
					controleBloqueioUsuario.setLoginSucesso(Boolean.TRUE);
					controleBloqueioUsuarioService.save(controleBloqueioUsuario);
				} else {
					JSFUtil.addGlobalMessage("msg_usuario_bloqueado", FacesMessage.SEVERITY_WARN);
					return null;
				}
			} else if(controleBloqueioUsuario.getTentativa() == 6 && !controleBloqueioUsuario.getBloqueado()){
				if(!logaUser()){
					controleBloqueioUsuario.setBloqueado(Boolean.FALSE);
					controleBloqueioUsuarioService.save(controleBloqueioUsuario);
					controleBloqueioUsuarioService.save(new ControleBloqueioUsuario(usuario));
					return null;
				}
				controleBloqueioUsuario.setBloqueado(Boolean.FALSE);
				controleBloqueioUsuario.setLoginSucesso(Boolean.TRUE);
				controleBloqueioUsuarioService.save(controleBloqueioUsuario);
			} else {
				if(!logaUser()){
					Integer tentativa = controleBloqueioUsuario.getTentativa();
					controleBloqueioUsuario.setTentativa(++tentativa);
					if(controleBloqueioUsuario.getTentativa() == 6){
						controleBloqueioUsuario.setBloqueado(Boolean.TRUE);
					}
					controleBloqueioUsuarioService.save(controleBloqueioUsuario);
					return null;
				}
				controleBloqueioUsuario.setBloqueado(Boolean.FALSE);
				controleBloqueioUsuario.setLoginSucesso(Boolean.TRUE);
				controleBloqueioUsuarioService.save(controleBloqueioUsuario);
			}
			
		}  else {
			if(!logaUser()){
				controleBloqueioUsuarioService.save(new ControleBloqueioUsuario(usuario));
				return null;
			}
		}
		return "/home.xhtml";
	}

	
	public boolean isUsuarioLogado() {
		HttpServletRequest request = (HttpServletRequest) FacesUtils.getServletRequest();
		return request.getSession().getAttribute("usuario") != null;
	}
	

	private Boolean validCampos(){
		return usuario.getLogin().equals("") || usuario.getSenha().equals("") ? false : true;
	}
	
	private Boolean logaUser(){
		return authenticationService.login(usuario.getLogin(), usuario.getSenha()) ? true : false;
	}
	
	public String logout() {
		authenticationService.logout();
		HttpServletRequest request = (HttpServletRequest) FacesUtils.getServletRequest();
		request.getSession().invalidate();
		return "logout";
	}

	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	public String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy - EEEE");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public Usuario getUsuario() {
		if(usuario == null){
			usuario = new Usuario();
		}
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setControleSenhaService(ControleSenhaService controleSenhaService) {
		this.controleSenhaService = controleSenhaService;
	}

	public UsuarioService getService() {
		return service;
	}

	public void setService(UsuarioService service) {
		this.service = service;
	}

	public void setControleBloqueioUsuarioService(ControleBloqueioUsuarioService controleBloqueioUsuarioService) {
		this.controleBloqueioUsuarioService = controleBloqueioUsuarioService;
	}
}