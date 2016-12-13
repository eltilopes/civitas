package br.com.civitasTest.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import br.com.civitas.acesso.UsuarioBean;
import br.com.civitas.arquitetura.base.Usuario;
import br.com.civitas.arquitetura.base.service.ControleSenhaService;
import br.com.civitas.arquitetura.base.service.UsuarioService;
import br.com.civitas.arquitetura.util.Digest;
import br.com.civitasTest.arqchitecture.AbstractContextTest;
import br.com.civitasTest.arqchitecture.TestUtil;
import br.com.civitasTest.arqchitecture.WebContextTestExecutionListener;
import br.com.civitasTest.testConfig.ComponentTestConfig;
import br.com.civitasTest.testConfig.HibernateTestConfig;
import br.com.civitasTest.testConfig.JpaTestConfig;
import br.com.civitasTest.testConfig.SecurityTestConfig;

@Transactional(readOnly = true)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@ContextConfiguration(classes={ComponentTestConfig.class, HibernateTestConfig.class, JpaTestConfig.class, SecurityTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({WebContextTestExecutionListener.class,	DependencyInjectionTestExecutionListener.class,	DirtiesContextTestExecutionListener.class})
public class TestAlteraSenha extends AbstractContextTest {
	
	private UsuarioBean usuarioBean;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private ControleSenhaService controleSenhaService;
	
	public TestAlteraSenha(){
		super("dataSetTestAlteraSenha.xml");
	}
	
	@Before
	public void init(){
		TestUtil.logarUsuario(getAuthenticationService(), getUsuarioService(), getControleSenhaService(), getControleBloqueioUsuarioService());
		usuarioBean = new UsuarioBean();
		usuarioBean.setService(usuarioService);
		usuarioBean.setControleSenhaService(controleSenhaService);
//		usuarioBean.initBean();
	}
	
	@Test
	public void testeSalvar(){
		usuarioBean.setSenhaAtual("1234");
		usuarioBean.setNovaSenha("4321");
		usuarioBean.setNovaSenhaRepita("4321");
//		usuarioBean.alteraSenha();
		Assert.assertTrue("Nao atualizou a senha corretamente", getByLogin(1L).getSenha().equals(Digest.MD5digest("4321")));
	}
	

	private Usuario getByLogin(Long id){
		return usuarioService.findByID(new Usuario(id));
	}
}