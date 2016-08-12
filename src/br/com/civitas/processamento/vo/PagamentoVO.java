package br.com.civitas.processamento.vo;

import java.text.SimpleDateFormat;
import java.util.Objects;

import br.com.civitas.processamento.entity.Pagamento;

public class PagamentoVO {

	public String nomeFuncionario;
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
		admissao = new SimpleDateFormat("dd/MM/yyyy").format(pagamento.getMatricula().getDataAdmissao());
		vinculo = Objects.nonNull(pagamento.getMatricula().getVinculo()) ? pagamento.getMatricula().getVinculo().getDescricao() : "";
		secretaria = Objects.nonNull(pagamento.getMatricula().getSecretaria()) ? pagamento.getMatricula().getSecretaria().getDescricao() : "";
		unidadeTrabalho = Objects.nonNull(pagamento.getMatricula().getUnidadeTrabalho()) ? pagamento.getMatricula().getUnidadeTrabalho().getDescricao() : "";
		nivelPagamento = Objects.nonNull(pagamento.getMatricula().getNivelPagamento()) ? pagamento.getMatricula().getNivelPagamento().getDescricao() : "";
		cargaHorariaPagamento = Objects.nonNull(pagamento.getMatricula().getCargaHorariaPagamento()) ? pagamento.getMatricula().getCargaHorariaPagamento().getDescricao() : "";
		setor = Objects.nonNull(pagamento.getMatricula().getSetor()) ? pagamento.getMatricula().getSetor().getDescricao() : "";
		funcao = Objects.nonNull(pagamento.getMatricula().getCargo()) ? pagamento.getMatricula().getCargo().getDescricao() : "";
		diasTrabalhados = pagamento.getDiasTrabalhados();
		proventos = String.format("%.2f", pagamento.getTotalProventos());
	}
	
	public static String getNomeColuna(String fieldName){
		switch (fieldName) {
		case "nomeFuncionario":
			return nomeFuncionarioColuna();
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
	
	public static String admissaoColuna(){
		return "DT. ADM.";
	}
	
	public static String vinculoColuna(){
		return "VÍNCULO";
	}
	
	public static String secretariaColuna(){
		return "SECRETARIA";
	}
	
	public static String unidadeTrabalhoColuna(){
		return "UNID. TRAB.";
	}
	
	public static String nivelPagamentoColuna(){
		return "NÍVEL PAG.";
	}
	
	public static String cargaHorariaPagamentoColuna(){
		return "CH";
	}
	
	public static String setorColuna(){
		return "SETOR";
	}
	
	public static String funcaoColuna(){
		return "FUNÇÃO";
	}

	public static String diasTrabalhadosColuna(){
		return "DIAS TRAB.";
	}
	
	public static String proventosColuna(){
		return "PROVENTOS";
	}

}
