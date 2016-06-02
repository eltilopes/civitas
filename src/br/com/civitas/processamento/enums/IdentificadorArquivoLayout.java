package br.com.civitas.processamento.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum IdentificadorArquivoLayout {

		// 0 - IDENTIFICADOR
		INICIO_EVENTO("PROVENTOS VALORREF DESCONTOS VALORREF"),
		FIM_EVENTO("LÍQUIDO: DESCONTOS:REMUNERAÇÃO: PROVENTOS:"), 
		ESPACO_NA_LINHA(" ")	,	
		VIRGULA(","), 
		PARENTESE_INICIAL("("), 
		PARENTESE_FINAL(")"), 
		PORCENTAGEM("%"), 
		CARGO("CARGO: CARGO 2:"),	
		CONTA_CORRENTE("C/C:"),	
		NIVEL("NIVEL"),	
		DAS_3("DAS-3"),	
		CPF("CPF"),	
		VINCULO("VÍNCULO:"),
		DATA_INICIO_FIM("DATA INÍCIO: DATA FIM:"),		
		PEB_I("PEB I"),		
		IRRF("I R R F"),	
		CARGA_HORARIA("C.H.:"),
		PENSAO_ALIMENTICIA("PENSAO ALIM."),		
		AREA_ATUACAO("ÁREA DE ATUAÇÃO:"),		
		ADICIONAL_FERIAS("ADICIONAL DE FERIAS"),		
		TERCO_FERIAS("1/3 DE FERIAS"),		
		UNIDADE_TRABALHO("U.TRAB.:"),		
		TOTAIS_PAGAMENTO("LÍQUIDO: DESCONTOS:REMUNERAÇÃO: PROVENTOS:"),		
		DATA_ADMISSAO("ADM:");
	
		private String descricao;
		
		public List<String> getDescricoes(){
			IdentificadorArquivoLayout[] list = IdentificadorArquivoLayout.values(); 
			return Arrays.asList(list).stream().map(i -> i.getDescricao()).collect(Collectors.toCollection(ArrayList<String> :: new));
		}
		
		private IdentificadorArquivoLayout(String descricao){
			this.descricao  = descricao;
		}
		
		public String getDescricao(){
			return descricao;
		}

	}
