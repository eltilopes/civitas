package br.com.civitas.processamento.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Identificador {

		// 0 - IDENTIFICADOR
		INICIO_EVENTO("PROVENTOS VALORREF DESCONTOS VALORREF"),
		FIM_EVENTO("L�QUIDO: DESCONTOS:REMUNERA��O: PROVENTOS:"), 
		ESPACO_NA_LINHA(" ")	,	
		VIRGULA(","), 
		PORCENTAGEM("%"), 
		CARGO("CARGO: CARGO 2:"),	
		CONTA_CORRENTE("C/C:"),	
		VINCULO("V�NCULO:"),
		DATA_INICIO_FIM("DATA IN�CIO: DATA FIM:"),		
		PEB_I("PEB I"),		
		IRRF("I R R F"),	
		CARGA_HORARIA("C.H.:"),
		PENSAO_ALIMENTICIA("PENSAO ALIM."),		
		AREA_ATUACAO("�REA DE ATUA��O:"),		
		ADICIONAL_FERIAS("ADICIONAL DE FERIAS"),		
		UNIDADE_TRABALHO("U.TRAB.:"),		
		TOTAIS_PAGAMENTO("L�QUIDO: DESCONTOS:REMUNERA��O: PROVENTOS:"),		
		DATA_ADMISSAO("ADM:");
	
		private String descricao;
		
		public List<String> getDescricoes(){
			Identificador[] list = Identificador.values(); 
			return Arrays.asList(list).stream().map(i -> i.getDescricao()).collect(Collectors.toCollection(ArrayList<String> :: new));
		}
		
		private Identificador(String descricao){
			this.descricao  = descricao;
		}
		
		public String getDescricao(){
			return descricao;
		}

	}
