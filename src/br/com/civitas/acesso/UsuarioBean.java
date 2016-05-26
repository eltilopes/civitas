package br.com.civitas.acesso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.model.DualListModel;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.arquitetura.base.Perfil;
import br.com.civitas.arquitetura.base.Usuario;
import br.com.civitas.arquitetura.base.service.ControleBloqueioUsuarioService;
import br.com.civitas.arquitetura.base.service.ControleSenhaService;
import br.com.civitas.arquitetura.base.service.UsuarioService;
import br.com.civitas.arquitetura.controller.AbstractCrudBean;
import br.com.civitas.arquitetura.seguranca.service.PerfilService;
import br.com.civitas.arquitetura.util.Digest;
import br.com.civitas.arquitetura.util.FacesUtils;

@ManagedBean(name="userBean")
@ViewScoped
public class UsuarioBean extends AbstractCrudBean<Usuario, UsuarioService> implements Serializable {

	private static final long serialVersionUID = -1747596691769224085L;

		@ManagedProperty(value="#{perfilService}")
		private PerfilService perfilService;
		
		@ManagedProperty(value="#{usuarioService}")
		private UsuarioService service;

		@ManagedProperty(value="#{controleSenhaService}")
		private ControleSenhaService controleSenhaService;
		
		@ManagedProperty(value="#{controleBloqueioUsuarioService}")
		private ControleBloqueioUsuarioService controleBloqueioUsuarioService; 

		private String novaSenha;
		private String novaSenhaRepita;
		private String senhaAtual;
		private String email;
		
		private DualListModel<Perfil> perfis;
		
		public void alteraSenha(ActionEvent ev) {
			try {

				setEntity((Usuario) FacesUtils.pegaAtributoSessao("usuario"));
				
				if (!(Digest.MD5digest(senhaAtual)).equals(getEntity().getSenha()))
					throw new ApplicationException("A senha atual não confere com a senha informada!");

				if (!novaSenha.equals(novaSenhaRepita))
					throw new ApplicationException("A nova senha não confere, tente novamente!");

				getEntity().setSenha(novaSenha);
				
				getService().atualizarSenhaToLogin(getEntity());
				FacesUtils.addInfoMessage("Senha alterada com sucesso! ");
				
			} catch (ApplicationException e) {
				FacesUtils.addErrorMessage(e.getMessage());
			} catch (Exception e) {
				FacesUtils.addErrorMessage("Digite a nova senha.");
			}
			setEntity(new Usuario());
		}
		
		public void recuperarSenha(ActionEvent e) {
			try {
				getService().reiniciarSenha(getEntity());
				FacesUtils.addInfoMessage("Em instantes você receberá um email com uma senha temporária e com informações para alteração da senha.");
				setEntity(new Usuario());
			} catch (ApplicationException ex) {
				ex.printStackTrace();
				FacesUtils.addErrorMessage(ex.getMessage());
			} catch (Exception ex) {
				FacesUtils.addErrorMessage("Erro: Ocorreu um problema durante a recuperação da senha. Tente novamente mais tarde ou contate o administrador do sistema. ");
				ex.printStackTrace();
			}
		}
		
		@Override
		public void update(ActionEvent event) {
			validar();
			for (Perfil perfil : getPerfis().getTarget()) {
				if (!getEntity().getPerfis().contains(perfil)) {
					getEntity().getPerfis().add(perfil);
				}
			}
			getEntity().getPerfis().removeAll(getPerfis().getSource());
			super.update(event);
		}
		
		@Override
		public void executeSave() {
			getEntity().setPerfis(new ArrayList<Perfil>());
			try {
				validar();
				if (FacesUtils.isMessageErro()) return;

				for (Perfil perfil : getPerfis().getTarget()) {
					getEntity().getPerfis().add(perfil);
				}
				String senha = geraSenha();
				getEntity().setSenha(Digest.MD5digest(senha));
				
				try {
					getService().enviarEmail(getEntity().getEmail(), getEntity().getLogin(), senha);
				} catch (Exception e) {
					e.printStackTrace();
					FacesUtils.addErrorMessage("Falha no envio do email.");
					try {
						getService().atualizarSenhaToLogin(getEntity());
					} catch (Exception e2) {
						e2.printStackTrace();	
					}
				}
				super.executeSave();
				
			} catch (ApplicationException e) {
				e.printStackTrace();
				FacesUtils.addErrorMessage(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				FacesUtils.addErrorMessage(getMessage("ERROR_INSERT"));
			}
		}
		
		public String geraSenha() {

			String[] carct = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
					"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
					"M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
					"Y", "Z" };
			String senha = "";
			for (int x = 0; x < 6; x++) {
				senha += carct[(int) (Math.random() * carct.length)];
			}
			return senha;
		}
		
		private void validar() {
			List<Perfil> target = getPerfis().getTarget();
			if (target == null || target.isEmpty()) {
				FacesUtils.addErrorMessage("É obrigatório informar pelo menos um perfil de usuários.");
			}
		}
		
		@Override
		public void prepareUpdate(ActionEvent event) {
			super.prepareUpdate(event);
			List<Perfil> todosPerfis = perfilService.buscarTodosAtivos();
			List<Perfil> perfisUsuario = new ArrayList<Perfil>();
			perfisUsuario = getEntity().getPerfis();
			todosPerfis.removeAll(perfisUsuario);
			perfis = new DualListModel<Perfil>(todosPerfis, perfisUsuario);
		}
		
		@Override
		public void prepareInsert(ActionEvent event) {
			super.prepareInsert(event);
			List<Perfil> todosPerfis = perfilService.buscarTodosAtivos();
			perfis = new DualListModel<Perfil>(todosPerfis, getEntity().getPerfis());
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

		public void setService(UsuarioService service) {
			super.setService(service);
			this.service = service;
		}

		public void setPerfilService(PerfilService perfilService) {
			this.perfilService = perfilService;
		}

		public void setControleSenhaService(ControleSenhaService controleSenhaService) {
			this.controleSenhaService = controleSenhaService;
		}

		public void setControleBloqueioUsuarioService(ControleBloqueioUsuarioService controleBloqueioUsuarioService) {
			this.controleBloqueioUsuarioService = controleBloqueioUsuarioService;
		}

		public DualListModel<Perfil> getPerfis() {
			return perfis;
		}

		public void setPerfis(DualListModel<Perfil> perfis) {
			this.perfis = perfis;
		}

	}