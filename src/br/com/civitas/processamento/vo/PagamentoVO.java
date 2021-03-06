package br.com.civitas.processamento.vo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import br.com.civitas.processamento.entity.Pagamento;

public class PagamentoVO {

	public String nomeFuncionario;
	public String numeroMatricula;
	public String cargaHoraria;
	public String admissao;
	public String vinculo;
	public String secretaria;
	public String unidadeTrabalho;
	public String nivelPagamento;
	public String cargaHorariaPagamento;
	public String setor;
	public String funcao;
	public String diasTrabalhados;
	public String proventos;
	
	public PagamentoVO(boolean field){
		nomeFuncionario = nomeFuncionarioColuna();
		numeroMatricula = numeroMatriculaColuna();
		cargaHoraria = cargaHorariaColuna();
		admissao = admissaoColuna();
		vinculo = vinculoColuna();
		secretaria = secretariaColuna();
		unidadeTrabalho = unidadeTrabalhoColuna();
		nivelPagamento = nivelPagamentoColuna();
		cargaHorariaPagamento = cargaHorariaPagamentoColuna();
		setor = setorColuna();
		funcao = funcaoColuna();
		diasTrabalhados = diasTrabalhadosColuna();
		proventos = proventosColuna();
	}

	public PagamentoVO(Pagamento pagamento) {
		nomeFuncionario = pagamento.getMatricula().getNomeFuncionario();
		numeroMatricula = pagamento.getMatricula().getNumeroMatricula();
		cargaHoraria = pagamento.getMatricula().getMatriculaPagamento().getCargaHoraria()+"";
		admissao = Objects.nonNull(pagamento.getMatricula().getDataAdmissao()) ? new SimpleDateFormat("dd/MM/yyyy").format(pagamento.getMatricula().getDataAdmissao()) : "";
		vinculo = Objects.nonNull(pagamento.getMatricula().getMatriculaPagamento().getVinculo()) ? pagamento.getMatricula().getMatriculaPagamento().getVinculo().getDescricao() : "";
		secretaria = Objects.nonNull(pagamento.getMatricula().getMatriculaPagamento().getSecretaria()) ? pagamento.getMatricula().getMatriculaPagamento().getSecretaria().getDescricao() : "";
		unidadeTrabalho = Objects.nonNull(pagamento.getMatricula().getMatriculaPagamento().getUnidadeTrabalho()) ? pagamento.getMatricula().getMatriculaPagamento().getUnidadeTrabalho().getDescricao() : "";
		nivelPagamento = Objects.nonNull(pagamento.getMatricula().getMatriculaPagamento().getNivelPagamento()) ? pagamento.getMatricula().getMatriculaPagamento().getNivelPagamento().getDescricao() : "";
		cargaHorariaPagamento = Objects.nonNull(pagamento.getMatricula().getMatriculaPagamento().getCargaHorariaPagamento()) ? pagamento.getMatricula().getMatriculaPagamento().getCargaHorariaPagamento().getDescricao() : "";
		setor = Objects.nonNull(pagamento.getMatricula().getMatriculaPagamento().getSetor()) ? pagamento.getMatricula().getMatriculaPagamento().getSetor().getDescricao() : "";
		funcao = Objects.nonNull(pagamento.getMatricula().getMatriculaPagamento().getCargo()) ? pagamento.getMatricula().getMatriculaPagamento().getCargo().getDescricao() : "";
		diasTrabalhados = Objects.nonNull(pagamento.getDiasTrabalhados()) ? pagamento.getDiasTrabalhados().toString() : "";
		proventos = String.format("%.2f", pagamento.getTotalProventos());
	}
	
	
	public static String getNomeColuna(String fieldName){
		switch (fieldName) {
		case "nomeFuncionario":
			return nomeFuncionarioColuna();
		case "numeroMatricula":
			return numeroMatriculaColuna();
		case "cargaHoraria":
			return cargaHorariaColuna();
		case "admissao":
			return admissaoColuna();
		case "vinculo":
			return vinculoColuna();
		case "secretaria":
			return secretariaColuna();
		case "unidadeTrabalho":
			return unidadeTrabalhoColuna();
		case "nivelPagamento":
			return nivelPagamentoColuna();
		case "cargaHorariaPagamento":
			return cargaHorariaPagamentoColuna();
		case "setor":
			return setorColuna();
		case "funcao":
			return funcaoColuna();
		case "diasTrabalhados":
			return diasTrabalhadosColuna();
		case "proventos":
			return proventosColuna();
		};
		return "";
	}
	
	public static String nomeFuncionarioColuna(){
		return "NOME FUNC.";
	}
	
	public static String numeroMatriculaColuna(){
		return "NUM. MAT.";
	}
	
	public static String cargaHorariaColuna(){
		return "CARGA HOR.";
	}
	
	public static String admissaoColuna(){
		return "DT. ADM.";
	}
	
	public static String vinculoColuna(){
		return "V?NCULO";
	}
	
	public static String secretariaColuna(){
		return "SECRETARIA";
	}
	
	public static String unidadeTrabalhoColuna(){
		return "UNID. TRAB.";
	}
	
	public static String nivelPagamentoColuna(){
		return "N?VEL PAG.";
	}
	
	public static String cargaHorariaPagamentoColuna(){
		return "CH";
	}
	
	public static String setorColuna(){
		return "SETOR";
	}
	
	public static String funcaoColuna(){
		return "FUN??O";
	}

	public static String diasTrabalhadosColuna(){
		return "DIAS TRAB.";
	}
	
	public static String getDiasTrabalhadosColuna(){
		return "DIAS TRAB.";
	}
	
	public static String proventosColuna(){
		return "PROVENTOS";
	}

	public static ArrayList<String> getNomesColunaSemDouble() {
		String[] array = {nomeFuncionarioColuna(),
				 numeroMatriculaColuna(),
				 cargaHorariaColuna(),
				 admissaoColuna(),
				 vinculoColuna(),
				 secretariaColuna(),
				 unidadeTrabalhoColuna(),
				 nivelPagamentoColuna(),
				 cargaHorariaPagamentoColuna(),
				 setorColuna(),
				 funcaoColuna(),
				 diasTrabalhadosColuna()};
		return new ArrayList<String>(Arrays.asList(array));
	}

}
