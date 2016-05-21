package br.com.civitas.arquitetura.seguranca.filter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.arquitetura.base.Permissao;
import br.com.civitas.arquitetura.util.PropertiesUtils;

public class Acesso implements Serializable{

	private static final long serialVersionUID = -7051862630804046345L;
	
	private String transacao;
	private Map<String, Permissao> Permissao = new HashMap<String, Permissao>();

	/**Local onde fica o arquivo properties de configuração do módulo.*/
	public static final String filePermissao = "/conf.properties";
	
	public static final String filePages = "/pages.properties";
	
	private static final Properties PERM_PROPERTIES = PropertiesUtils.getProperties(filePermissao);
	
	private static final Properties PAGES_PROPERTIES = PropertiesUtils.getProperties(filePages);

	public Acesso( Map<String, Permissao> Permissao ) {
		configuraPermissao( Permissao );
	}

	public void configuraPermissao( Map<String, Permissao> Permissao ){		
		if( Permissao != null ){
			this.Permissao = Permissao;
		}
	}

	public static String[] getPagesNoFilter(){
		String noFilter =  (String) PERM_PROPERTIES.get("NO_FILTER" );
		if( noFilter == null )
			return null;		
		return noFilter.split( "," );
	}
	
	public static String[] getDirectoriesNoFilter(){
		String noFilter =(String) PERM_PROPERTIES.get( "DIR_NO_FILTER" );
		if( noFilter == null )
			return null;		
		return noFilter.split( "," );
	}
	
	public static String getLoginPage(){
		String login = (String) PERM_PROPERTIES.get("LOGIN" );
		return login != null ? login.trim() : null;
	}
	
	public static String getHomePage(){
		String home = (String) PERM_PROPERTIES.get("HOME" );
		return home != null ? home.trim() : null;
	}
	
	public static String getNoAccessPage(){
		String home = (String) PERM_PROPERTIES.get("NO_ACCESS" );
		if(home == null){
			throw new ApplicationException("Página de acesso indevido não definida no arquivo conf.properties: NO_ACCESS is not defined in file conf.properties.");
		}
		return home.trim();
	}
	
	public static String getKeyPage( String pagina ){
		String chave = (String) PAGES_PROPERTIES.get( pagina );
		return chave != null ? chave.trim() : null;
	}
	
	public static Properties getPages(){
		return PAGES_PROPERTIES;
	}
	
	/**
	 * Função p/ liberar as permissões de acesso
	 * 
	 * @param transacao
	 * @param editar
	 * @param delete
	 * @param inserir
	 * @return
	 */
	public boolean isPermissao() {		
		if( transacao == null )
			throw new IllegalArgumentException( "Transação não informada." );

		Permissao p = Permissao.get( transacao );
		if( p != null ){
			return true;
		}
		return false;
	}

	/**
	 * Informa se a uma operação básica é permitida para a transação.
	 * @param tipo
	 * @return
	 */
	public boolean isOpcaoPermitida(int tipo) {
		if( transacao == null )
			throw new IllegalArgumentException( "Transação não informada." );

		Permissao p = Permissao.get( transacao );
		return p != null;
	}

	/**
	 * Informa se tem permissão de inserir. na transação.
	 * @return
	 */
	public boolean isInsert() {
		return isOpcaoPermitida(1);
	}

	/**
	 * Informa se tem permissão de atualizar, na transação.
	 * @return
	 */
	public boolean isUpdate() {
		return isOpcaoPermitida(2);
	}

	/**
	 * Informa se tem permissão de remover, na transação.
	 * @return
	 */
	public boolean isRemove() {
		return isOpcaoPermitida(3);
	}
	
	/**
	 * Monta um mapa com as permissões do módulo.
	 * @return
	 */
	public Map<String, Acesso> mapeiaPermissao(){
		Map<String, Acesso> mapa = new HashMap<String, Acesso>();
		if( Permissao == null ){
			throw new ApplicationException( "Permissões não informadas." );
		}
		for( Permissao p : Permissao.values() ){
			String chaveTransacao = p.getChave();
			Acesso a = new Acesso( Permissao );			
			a.setTransacao( chaveTransacao );
			mapa.put( chaveTransacao, a );
		}				
		return mapa;
	}
	
	public String getTransacao() {
		return transacao;
	}

	public void setTransacao(String transacao) {
		this.transacao = transacao;
	}
}
