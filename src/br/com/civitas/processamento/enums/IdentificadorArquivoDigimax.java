package br.com.civitas.processamento.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum IdentificadorArquivoDigimax {

		// 0 - IDENTIFICADOR
		INICIO_EVENTO("| COD | TIPO VANTAGEM/DESCONTO"),
		FIM_EVENTO("INSS CONT.     TOT.DEP.           TOT.VANT.           TOT.DESC.         TOT.LIQUIDO"), 
		INICIO_QUEBRA_PAGINA("======================================================================================================================D I G I M A X"),
		FIM_QUEBRA_PAGINA("===================================================================================================================================="), 
		NOME_FUNCIONARIO("NOME DO FUNCIONARIO")	,	
		ESPACO_NA_LINHA(" ")	,	
		VIRGULA(","), 
		HIFEN("-"), 
		PIPE("|"), 
		BARRA("/"), 
		CPF("CPF:"), 
		SECRETARIA("SECRETARIA..:"),	
		DEPARTAMENTO("DEPARTAMENTO:"),	
		TOTAL("TOTAL"),	
		FERIAS("FERIAS"),	
		ADICIONAL("ADICIONAL"),	
		ADICIONAL_ABREVIADO("ADIC."),	
		VINCULO("VINCULO:"),
		TOTAIS_PAGAMENTO("INSS CONT.     TOT.DEP.           TOT.VANT.           TOT.DESC.         TOT.LIQUIDO"),
		FIM_TOTAIS_PAGAMENTO("------------------------------------------------------------------------------------------------------------------------------------"),
		DATA_ADMISSAO("DT. ADM:"),
		DATA_AFASTAMENTO("DT. AFASTAMENTO:"), 
		INICIO_RESUMO("TOTAL DO DEPARTAMENTO"), 
		FIM_RESUMO("----------------------------------------------------------------------------------"), 
		TOTAL_DESCONTOS("Total de descontos do departamento"), 
		SALARIO_BASE("SALARIO BASE"), 
		TOTAL_PROVENTOS("Total de vantagens do departamento");
	
		private String descricao;
		
		public List<String> getDescricoes(){
			IdentificadorArquivoDigimax[] list = IdentificadorArquivoDigimax.values(); 
			return Arrays.asList(list).stream().map(i -> i.getDescricao()).collect(Collectors.toCollection(ArrayList<String> :: new));
		}
		
		private IdentificadorArquivoDigimax(String descricao){
			this.descricao  = descricao;
		}
		
		public String getDescricao(){
			return descricao;
		}

	}
