package br.com.civitas.acesso;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.arquitetura.base.Usuario;
import br.com.civitas.arquitetura.base.service.ControleBloqueioUsuarioService;
import br.com.civitas.arquitetura.base.service.ControleSenhaService;
import br.com.civitas.arquitetura.base.service.UsuarioService;
import br.com.civitas.arquitetura.controller.AbstractCrudBean;
import br.com.civitas.arquitetura.seguranca.service.PerfilService;
import br.com.civitas.arquitetura.util.FacesUtils;
import br.com.civitas.arquitetura.util.Mail;

@ManagedBean(name = "userBean")
@ViewScoped
public class UsuarioBean extends AbstractCrudBean<Usuario, UsuarioService> implements Serializable {

	private static final long serialVersionUID = -2000346335633381274L;

		@ManagedProperty(value="#{perfilService}")
		private PerfilService perfilService;
		
		@ManagedProperty(value="#{usuarioService}")
		private UsuarioService usuarioService;

		@ManagedProperty(value="#{controleSenhaService}")
		private ControleSenhaService controleSenhaService;
		
		@ManagedProperty(value="#{mail}")
		private Mail mail;
		
		@ManagedProperty(value="#{controleBloqueioUsuarioService}")
		private ControleBloqueioUsuarioService controleBloqueioUsuarioService; 

		private String novaSenha;
		private String novaSenhaRepita;
		private String senhaAtual;
		private String email;
		
		public void alteraSenha(ActionEvent ev) {
			try {

				setEntity((Usuario) FacesUtils.pegaAtributoSessao("usuario"));
				
				if (!(senhaAtual).equals(getEntity().getSenha()))
					throw new ApplicationException("A senha atual não confere com a senha informada!");

				if (!novaSenha.equals(novaSenhaRepita))
					throw new ApplicationException("A nova senha não confere, tente novamente!");

				getEntity().setSenha(novaSenha);
				
				getService().atualizarSenhaToLogin(getEntity());
				FacesUtils.addInfoMessage("Senha alterada com sucesso! \n Lembrando que essa senha será tambem sua nova senha do SGA");
				
			} catch (ApplicationException e) {
				FacesUtils.addErrorMessage(e.getMessage());
			} catch (Exception e) {
				FacesUtils.addErrorMessage("Digite a nova senha.");
			}
			setEntity(new Usuario());
		}
		
		public String getNovaSenha() {
			return novaSenha;
		}

		public void setNovaSenha(String novaSenha) {
			this.novaSenha = novaSenha;
		}

		public String getNovaSenhaRepita() {
			return novaSenhaRepita;
		}

		public void setNovaSenhaRepita(String novaSenhaRepita) {
			this.novaSenhaRepita = novaSenhaRepita;
		}

		public String getSenhaAtual() {
			return senhaAtual;
		}

		public void setSenhaAtual(String senhaAtual) {
			this.senhaAtual = senhaAtual;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public void setUsuarioService(UsuarioService usuarioService) {
			this.usuarioService = usuarioService;
		}

		public void setPerfilService(PerfilService perfilService) {
			this.perfilService = perfilService;
		}

		public void setControleSenhaService(ControleSenhaService controleSenhaService) {
			this.controleSenhaService = controleSenhaService;
		}

		public void setMail(Mail mail) {
			this.mail = mail;
		}

		public void setControleBloqueioUsuarioService(ControleBloqueioUsuarioService controleBloqueioUsuarioService) {
			this.controleBloqueioUsuarioService = controleBloqueioUsuarioService;
		}

	}