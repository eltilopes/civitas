package br.com.civitas.arquitetura.report;

import java.util.HashMap;
import java.util.Map;

public class ReportCommandFactory {

	
	private static ReportCommandFactory instance;
	
	//Guardará a instância do command de cada relatório.
	private Map<String, ReportCommand> cache;
	
	private ReportCommandFactory() {
		cache = new HashMap<String, ReportCommand>();
	}
	
	/**
	 * Pega a instância do ReportCommandFactory atual. Cria uma caso não exista instância.
	 * @return
	 */
	public static ReportCommandFactory getInstance(){
		
		if(instance==null)
			instance = new ReportCommandFactory();
		return instance; 
	}
	
	/**
	 * Cria uma instância de um objeto do tipo ReportCommand.
	 * @param command Full name do command a ser criado.
	 * @return Intância do command
	 */		
	public ReportCommand getCommand(String command){
		
		if(command == null)
			throw new IllegalArgumentException("Informe o fullname do ReportCommand a ser criado");
		
		Object cmd = cache.get(command);
		
		if(cmd != null)
			return(ReportCommand)cmd;
		
		try {
			cmd = Class.forName(command).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro na tentativa de instanciar o command: " + command,e);
		}
			
		if(!(cmd instanceof ReportCommand) )
			throw new  IllegalArgumentException(command + " deve implementar a interface ReportCommand"); 
				
		cache.put(command, (ReportCommand)cmd);

		return (ReportCommand)cmd;
	}
}
