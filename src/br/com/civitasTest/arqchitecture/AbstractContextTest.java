package br.com.civitasTest.arqchitecture;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import br.com.civitas.arquitetura.base.service.ControleBloqueioUsuarioService;
import br.com.civitas.arquitetura.base.service.ControleSenhaService;
import br.com.civitas.arquitetura.base.service.UsuarioService;
import br.com.civitas.arquitetura.service.AuthenticationService;
import br.com.civitas.arquitetura.util.FileUtil;

@Component
public class AbstractContextTest {
	
	@PersistenceContext
	private EntityManager entityManager;
	private IDatabaseConnection conn;
	private IDataSet dataSet;
	private String dataSetName;
	
	@Autowired
	private DriverManagerDataSource dataSource;
	
	@Autowired
	private ControleBloqueioUsuarioService controleBloqueioUsuarioService;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private ControleSenhaService controleSenhaService;
	
	public AbstractContextTest(){}
	
	public AbstractContextTest(String dataSetName) {
		this.dataSetName = dataSetName;
	}
	
	private void init() throws IOException, SQLException, DatabaseUnitException {
		TestUtil.logarUsuario(authenticationService, usuarioService, controleSenhaService, controleBloqueioUsuarioService);
//		FileInputStream fileInputStream = new FileInputStream(SuiteClasses.PATH_DATASET + dataSetName);
		FileInputStream fileInputStream = new FileInputStream( dataSetName);
		conn = new DatabaseConnection(dataSource.getConnection());
		dataSet = new FlatXmlDataSetBuilder().build(fileInputStream);
		
		try{
			DatabaseOperation.CLEAN_INSERT.execute(conn, dataSet);
		}finally{
			conn.close();
			fileInputStream.close();
		}
		
	}
	
	@Before
	public void beforeTest() throws IOException, SQLException, DatabaseUnitException{
		init();
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	public static void criarDiretorioSeNaoExiste(String diretorio){
		String diretorios[] = diretorio.split("\\\\");
		
		String subdiretorio = diretorios[0];
		
		for (int i = 1; i < diretorios.length; i++) {
			subdiretorio += "\\" + diretorios[i];
			new File(subdiretorio).mkdir();
		}
	}
	
	public static void deletarArquivosDiretorio(String ... diretorios){
		for (String diretorio : diretorios) {
			List<File> arquivos = FileUtil.getFileByPath(diretorio, "*.*");
			for (File file : arquivos) {
				file.delete();
			}
		}
	}

	public ControleBloqueioUsuarioService getControleBloqueioUsuarioService() {
		return controleBloqueioUsuarioService;
	}

	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	public UsuarioService getUsuarioService() {
		return usuarioService;
	}

	public ControleSenhaService getControleSenhaService() {
		return controleSenhaService;
	}
}