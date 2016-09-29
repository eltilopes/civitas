package br.com.civitas.processamento.factory;

import java.util.ArrayList;
import java.util.List;

import br.com.civitas.processamento.enums.TipoArquivo;

public class FactoryEnuns {
	
	public static List<TipoArquivo> listaTipoArquivo(){
		List<TipoArquivo> listaTipoArquivo = new ArrayList<TipoArquivo>();
		for (TipoArquivo tipo : TipoArquivo.values()) {
			listaTipoArquivo.add(tipo);
		}
		listaTipoArquivo.remove(TipoArquivo.NENHUM);
		listaTipoArquivo.sort((s1, s2) -> Integer.compare(s1.getDescricao().length(), s2.getDescricao().length()));
		return listaTipoArquivo;
	}

}
