package br.com.civitasTest.arqchitecture;

import br.com.civitas.acesso.LoginCivitasBean;
import br.com.civitas.arquitetura.base.Usuario;
import br.com.civitas.arquitetura.base.service.ControleBloqueioUsuarioService;
import br.com.civitas.arquitetura.base.service.ControleSenhaService;
import br.com.civitas.arquitetura.base.service.UsuarioService;
import br.com.civitas.arquitetura.service.AuthenticationService;

public class TestUtil {
	
	private static LoginCivitasBean loginCivitasBean;
	
	public static void logarUsuario(AuthenticationService authenticationService, UsuarioService usuarioService, ControleSenhaService controleSenhaService, ControleBloqueioUsuarioService controleBloqueioUsuarioService){
		loginCivitasBean = new LoginCivitasBean();
		Usuario user = new Usuario();
		user.setLogin("PARAIBA");
		user.setSenha("1234");
		setServicesMaisLogar(authenticationService, usuarioService,	controleSenhaService, controleBloqueioUsuarioService, user);
	}

	public static void logarUsuario(String usuario, String senha,AuthenticationService authenticationService, UsuarioService usuarioService, ControleSenhaService controleSenhaService, ControleBloqueioUsuarioService controleBloqueioUsuarioService){
		loginCivitasBean = new LoginCivitasBean();
		Usuario user = new Usuario();
		user.setLogin(usuario);
		user.setSenha(senha);
		setServicesMaisLogar(authenticationService, usuarioService,	controleSenhaService, controleBloqueioUsuarioService, user);
	}

	private static void setServicesMaisLogar(
			AuthenticationService authenticationService,
			UsuarioService usuarioService,
			ControleSenhaService controleSenhaService,
			ControleBloqueioUsuarioService controleBloqueioUsuarioService,
			Usuario user) {
		loginCivitasBean.setUsuario(user);
		loginCivitasBean.setAuthenticationService(authenticationService);
		loginCivitasBean.setService(usuarioService);
		loginCivitasBean.setControleSenhaService(controleSenhaService);
		loginCivitasBean.setControleBloqueioUsuarioService(controleBloqueioUsuarioService);
		loginCivitasBean.logar();
	}
	
	
}