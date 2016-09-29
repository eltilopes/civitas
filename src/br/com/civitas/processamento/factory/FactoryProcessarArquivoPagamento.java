package br.com.civitas.processamento.factory;

import org.springframework.stereotype.Component;

import br.com.civitas.arquitetura.util.FacesUtils;
import br.com.civitas.processamento.enums.TipoArquivo;
import br.com.civitas.processamento.interfac.IProcessarArquivoPagamento;

@Component
public class FactoryProcessarArquivoPagamento {

	@SuppressWarnings("incomplete-switch")
	public IProcessarArquivoPagamento getInstanciaProcessamento(TipoArquivo tipoArquivo){
		String service = "";
		switch (tipoArquivo) {
		case ARQUIVO_LAYOUT:
			service = "processarArquivoLayoutService";
			break;
		case ARQUIVO_INFORMATICA:
			service = "processarArquivoInformaticaService";
			break;	
		case ARQUIVO_TARGET:
			service = "processarArquivoTargetService";
			break;	
		case ARQUIVO_DIGIMAX:
			service = "processarArquivoDigimaxService";
			break;	
		}
		return (IProcessarArquivoPagamento) FacesUtils.getBean(service);
	}
}
