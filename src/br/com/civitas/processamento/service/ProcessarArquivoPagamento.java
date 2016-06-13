package br.com.civitas.processamento.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.helpers.utils.StringUtils;
import br.com.civitas.processamento.entity.ArquivoPagamento;
import br.com.civitas.processamento.entity.CargaHorariaPagamento;
import br.com.civitas.processamento.entity.Cargo;
import br.com.civitas.processamento.entity.Evento;
import br.com.civitas.processamento.entity.NivelPagamento;
import br.com.civitas.processamento.entity.Secretaria;
import br.com.civitas.processamento.entity.Setor;
import br.com.civitas.processamento.entity.UnidadeTrabalho;
import br.com.civitas.processamento.entity.Vinculo;
import br.com.civitas.processamento.utils.DiretorioProcessamento;

public abstract class ProcessarArquivoPagamento {
	
	@Autowired
	private  EventoService eventoService ;
	
	@Autowired
	private  CargoService cargoService;
	
	@Autowired
	private  SecretariaService secretariaService;
	
	@Autowired
	private  SetorService setorService;
	
	@Autowired
	private  UnidadeTrabalhoService unidadeTrabalhoService;
	
	@Autowired
	private  NivelPagamentoService nivelPagamentoService;
	
	@Autowired
	private  CargaHorariaPagamentoService cargaHorariaPagamentoService;
	
	@Autowired
	private  VinculoService vinculoService;
	
	private String nomeArquivoTemporario;
	private FileReader fileReaderEvento;
	private FileReader filReaderPagamento;
	private ArquivoPagamento arquivoPagamento;
	private PDDocument document;
	
	private  List<Evento> eventos ;
	private  List<Cargo> cargos ;
	private  List<Secretaria> secretarias ;
	private  List<UnidadeTrabalho> unidadesTrabalho;
	private  List<NivelPagamento> niveisPagamento ;
	private  List<CargaHorariaPagamento> cargasHorariaPagamento ;
	private  List<Setor> setores ;
	private  List<Vinculo> vinculos ;

	public void iniciarArquivos( ) {
		try {
			criarArquivoProcessamento();
			document = PDDocument.load(new File(nomeArquivoTemporario));
			document.getClass();
			if (!document.isEncrypted()) {
				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
				stripper.setSortByPosition(true);
				PDFTextStripper Tstripper = new PDFTextStripper();
				String conteudoArquivo = Tstripper.getText(document);
				String nomeArquivoTemporario = DiretorioProcessamento.getDiretorioTemporario() + "/" + arquivoPagamento.getNomeArquivo().substring(0, arquivoPagamento.getNomeArquivo().length() -3 ) + "txt";
				BufferedWriter buffWrite = new BufferedWriter(new FileWriter(nomeArquivoTemporario));
				buffWrite.append(conteudoArquivo);
				buffWrite.close();
				fileReaderEvento = new FileReader(nomeArquivoTemporario);
				filReaderPagamento = new FileReader(nomeArquivoTemporario);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void criarArquivoProcessamento() throws IOException {
		nomeArquivoTemporario = "";
		String filename = FilenameUtils.getName(new String(arquivoPagamento.getFile().getFileName().getBytes(Charset.defaultCharset()), "UTF-8"));
	    InputStream input = arquivoPagamento.getFile().getInputstream();
	    File file = new File(DiretorioProcessamento.getDiretorioTemporario(), filename);
	    OutputStream output = new FileOutputStream(file);
	    nomeArquivoTemporario = file.getAbsolutePath();
	    try {
	        IOUtils.copy(input, output);
	    } catch(Exception e){
	    	e.printStackTrace();
	    	nomeArquivoTemporario = null;
	    } finally {
	        IOUtils.closeQuietly(input);
	        IOUtils.closeQuietly(output);
	    }
	}

	public void finalizarArquivos( ) throws IOException {
		fileReaderEvento.close();
		filReaderPagamento.close();
		String filename = FilenameUtils.getName(new String(arquivoPagamento.getFile().getFileName().getBytes(Charset.defaultCharset()), "UTF-8"));
	    InputStream input = arquivoPagamento.getFile().getInputstream();
	    File file = new File(DiretorioProcessamento.getDiretorioProcessado(), filename);
	    OutputStream output = new FileOutputStream(file);
		IOUtils.copy(input, output);
		IOUtils.closeQuietly(input);
        IOUtils.closeQuietly(output);
		document.close();
		String nomeArquivo = DiretorioProcessamento.getDiretorioTemporario() + "/" + arquivoPagamento.getNomeArquivo().substring(0, arquivoPagamento.getNomeArquivo().length() -3 ) + "txt";
		File fileTxt = new File(nomeArquivo);  
		fileTxt.delete();
		nomeArquivo = DiretorioProcessamento.getDiretorioTemporario() + "/" + arquivoPagamento.getNomeArquivo();
		File filePdf = new File(nomeArquivo);  
		filePdf.delete();
	}

	public  void getEvento(String linha, String chave) {
		Evento evento = new Evento();
		Evento eventoAuxiliar = new Evento();
		try {
			evento.setChave(chave);
			evento.setCidade(arquivoPagamento.getCidade());;
			evento.setTipoArquivo(arquivoPagamento.getTipoArquivo());
			eventoAuxiliar = getEvento(evento);
			if(Objects.isNull(eventoAuxiliar)){
				evento.setNome(evento.getChave());
				evento= eventoService.save(evento);
				eventos.add(evento);
			}else{
				evento = eventoAuxiliar;
			}
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar Evento. Linha: " + linha);
		}
	}
	
	public  void getEvento(Evento evento,String linha) {
		Evento eventoAuxiliar = new Evento();
		try {
			eventoAuxiliar = getEvento(evento);
			if(Objects.isNull(eventoAuxiliar)){
				evento.setNome(evento.getChave());
				evento= eventoService.save(evento);
				eventos.add(evento);
			}else{
				evento = eventoAuxiliar;
			}
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar Evento. Linha: " + linha);
		}
	}
	
	private Evento getEvento(Evento evento) {
		for(Evento e : eventos){
			if(e.getCidade().getId().equals(evento.getCidade().getId()) 
					&& e.getTipoArquivo().getCodigo()==evento.getTipoArquivo().getCodigo()
					&& e.getChave().equals(evento.getChave())){
				evento = e;
				return evento;
			}
		}
		return null;
	}
	
	public Cargo getCargo(Cargo cargo, String linha) {
		Cargo cargoAuxiliar = new Cargo();
		try {
			cargoAuxiliar = getCargo(cargo);
			if(Objects.isNull(cargoAuxiliar)){
				cargo = cargoService.save(cargo);
				cargos.add(cargo);
			}else{
				cargo = cargoAuxiliar;
			}
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar Cargo. Linha: " + linha);
		}
		return cargo;
	}

	private Cargo getCargo(Cargo cargo) {
		for(Cargo c : cargos){
			if(c.getCidade().getId().equals(cargo.getCidade().getId()) && 
					   c.getTipoArquivo().getCodigo()==cargo.getTipoArquivo().getCodigo() && 
					   c.getNumero().equals(cargo.getNumero())){
				cargo = c;
				return cargo;
			}
		}
		return null;
	}
	
	public Secretaria getSecretaria(Secretaria secretaria, String linha) {
		Secretaria secretariaAuxiliar = new Secretaria();
		try {
			secretariaAuxiliar = getSecretaria(secretaria);
			if(Objects.isNull(secretariaAuxiliar)){
				secretaria = secretariaService.save(secretaria);
				secretarias.add(secretaria);
			}else{
				secretaria = secretariaAuxiliar;
			}
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar Secretaria. Linha: " + linha);
		}
		return secretaria;
	}
	
	private Secretaria getSecretaria(Secretaria secretaria) {
		for(Secretaria s : secretarias){
			if(s.getCidade().getId().equals(secretaria.getCidade().getId()) && 
					s.getTipoArquivo().getCodigo()==secretaria.getTipoArquivo().getCodigo() && 
					s.getDescricao().equals(secretaria.getDescricao())){
				secretaria = s;
				return secretaria;
			}
		}
		return null;
	}
	
	public UnidadeTrabalho getUnidadeTrabalho(UnidadeTrabalho unidadeTrabalho, String linha) {
		UnidadeTrabalho unidadeTrabalhoAuxiliar = new UnidadeTrabalho();
		try {
			unidadeTrabalhoAuxiliar = getUnidadeTrabalho(unidadeTrabalho);
			if(Objects.isNull(unidadeTrabalhoAuxiliar)){
				unidadeTrabalho = unidadeTrabalhoService.save(unidadeTrabalho);
				unidadesTrabalho.add(unidadeTrabalho);
			}else{
				unidadeTrabalho = unidadeTrabalhoAuxiliar;
			}
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar Unidade Trabalho. Linha: " + linha);
		}
		return unidadeTrabalho;
	}
	
	private UnidadeTrabalho getUnidadeTrabalho(UnidadeTrabalho unidadeTrabalho) {
		for(UnidadeTrabalho ut : unidadesTrabalho){
			if(ut.getCidade().getId().equals(unidadeTrabalho.getCidade().getId()) && 
					ut.getTipoArquivo().getCodigo()==unidadeTrabalho.getTipoArquivo().getCodigo() && 
					ut.getDescricao().equals(unidadeTrabalho.getDescricao()) &&
					ut.getCodigo().equals(unidadeTrabalho.getCodigo())){
				unidadeTrabalho = ut;
				return unidadeTrabalho;
			}
		}
		return null;
	}
	
	public CargaHorariaPagamento getCargaHorariaPagamento(CargaHorariaPagamento cargaHorariaPagamento, String linha) {
		CargaHorariaPagamento cargaHorariaPagamentoAuxiliar = new CargaHorariaPagamento();
		if(StringUtils.notNullOrEmpty(cargaHorariaPagamento.getDescricao()) &&
				StringUtils.notNullOrEmpty(cargaHorariaPagamento.getCodigo())){
			try {
				cargaHorariaPagamentoAuxiliar = getCargaHorariaPagamento(cargaHorariaPagamento);
				if(Objects.isNull(cargaHorariaPagamentoAuxiliar)){
					cargaHorariaPagamento = cargaHorariaPagamentoService.save(cargaHorariaPagamento);
					cargasHorariaPagamento.add(cargaHorariaPagamento);
				}else{
					cargaHorariaPagamento = cargaHorariaPagamentoAuxiliar;
				}
			} catch (Exception e) {
				throw new ApplicationException("Erro ao pegar Carga Horária Pagamento. Linha: " + linha);
			}
			return cargaHorariaPagamento;
		}
		return null;
	}
	
	private CargaHorariaPagamento getCargaHorariaPagamento(CargaHorariaPagamento cargaHorariaPagamento) {
		for(CargaHorariaPagamento chp : cargasHorariaPagamento){
			if(chp.getCidade().getId().equals(cargaHorariaPagamento.getCidade().getId()) && 
					chp.getTipoArquivo().getCodigo()==cargaHorariaPagamento.getTipoArquivo().getCodigo() && 
					chp.getDescricao().equals(cargaHorariaPagamento.getDescricao()) &&
					chp.getCodigo().equals(cargaHorariaPagamento.getCodigo())){
				cargaHorariaPagamento = chp;
				return cargaHorariaPagamento;
			}
		}
		return null;
	}
	
	public NivelPagamento getNivelPagamento(NivelPagamento nivelPagamento, String linha) {
		NivelPagamento nivelPagamentoAuxiliar = new NivelPagamento();
		if(StringUtils.notNullOrEmpty(nivelPagamento.getDescricao()) &&
				StringUtils.notNullOrEmpty(nivelPagamento.getCodigo())){
			try {
				nivelPagamentoAuxiliar = getNivelPagamento(nivelPagamento);
				if(Objects.isNull(nivelPagamentoAuxiliar)){
					nivelPagamento = nivelPagamentoService.save(nivelPagamento);
					niveisPagamento.add(nivelPagamento);
				}else{
					nivelPagamento = nivelPagamentoAuxiliar;
				}
			} catch (Exception e) {
				throw new ApplicationException("Erro ao pegar Nivel Pagamento. Linha: " + linha);
			}
			return nivelPagamento;
		}
		return null;
	}
	
	private NivelPagamento getNivelPagamento(NivelPagamento nivelPagamento) {
		for(NivelPagamento np : niveisPagamento){
			if(np.getCidade().getId().equals(nivelPagamento.getCidade().getId()) && 
					np.getTipoArquivo().getCodigo()==nivelPagamento.getTipoArquivo().getCodigo() && 
					np.getDescricao().equals(nivelPagamento.getDescricao()) &&
					np.getCodigo().equals(nivelPagamento.getCodigo())){
				nivelPagamento = np;
				return nivelPagamento;
			}
		}
		return null;
	}
	
	public Setor getSetor(Setor setor, String linha) {
		Setor setorAuxiliar = new Setor();
		try {
			setorAuxiliar = getSetor(setor);
			if(Objects.isNull(setorAuxiliar)){
				setor = setorService.save(setor);
				setores.add(setor);
			}else{
				setor = setorAuxiliar;
			}
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar Setor. Linha: " + linha);
		}
		return setor;
	}
	
	
	private Setor getSetor(Setor setor) {
		for(Setor s : setores){
			if(s.getCidade().getId().equals(setor.getCidade().getId()) && 
					s.getTipoArquivo().getCodigo()==setor.getTipoArquivo().getCodigo() && 
					s.getDescricao().equals(setor.getDescricao())){
				setor = s;
				return setor;
			}
		}
		return null;
	}
	
	public Vinculo getVinculo(Vinculo vinculo, String linha) throws ApplicationException {
		Vinculo vinculoAuxiliar = new Vinculo();
		try {
			vinculoAuxiliar = getVinculo(vinculo);
			if(Objects.isNull(vinculoAuxiliar)){
				vinculo = vinculoService.save(vinculo);
				vinculos.add(vinculo);
			}else{
				vinculo = vinculoAuxiliar;
			}
		} catch (Exception e) {
			throw new ApplicationException("Erro ao pegar o Vínculo. Linha: " + linha);
		}
		return vinculo;
	}

	private Vinculo getVinculo(Vinculo vinculo) {
		for(Vinculo v :vinculos){
			if(v.getCidade().getId().equals(vinculo.getCidade().getId()) 
					&& v.getNumero().equals(vinculo.getNumero())
					&& v.getDescricao().equals(vinculo.getDescricao())){
				vinculo = v;
				return vinculo;
			}
		}
		return null;
	}
	
	public FileReader getFileReaderEvento() {
		return fileReaderEvento;
	}

	public void setFileReaderEvento(FileReader fileReaderEvento) {
		this.fileReaderEvento = fileReaderEvento;
	}

	public FileReader getFilReaderPagamento() {
		return filReaderPagamento;
	}

	public void setFilReaderPagamento(FileReader filReaderPagamento) {
		this.filReaderPagamento = filReaderPagamento;
	}

	public ArquivoPagamento getArquivoPagamento() {
		return arquivoPagamento;
	}

	public void setArquivoPagamento(ArquivoPagamento arquivoPagamento) {
		this.arquivoPagamento = arquivoPagamento;
	}

	public List<Evento> getEventos() {
		return eventos;
	}

	public void setEventos(List<Evento> eventos) {
		this.eventos = eventos;
	}

	public List<Cargo> getCargos() {
		return cargos;
	}

	public void setCargos(List<Cargo> cargos) {
		this.cargos = cargos;
	}

	public List<Setor> getSetores() {
		return setores;
	}

	public void setSetores(List<Setor> setores) {
		this.setores = setores;
	}

	public List<Secretaria> getSecretarias() {
		return secretarias;
	}

	public void setSecretarias(List<Secretaria> secretarias) {
		this.secretarias = secretarias;
	}

	public List<Vinculo> getVinculos() {
		return vinculos;
	}

	public void setVinculos(List<Vinculo> vinculos) {
		this.vinculos = vinculos;
	}

	public List<UnidadeTrabalho> getUnidadesTrabalho() {
		return unidadesTrabalho;
	}

	public void setUnidadesTrabalho(List<UnidadeTrabalho> unidadesTrabalho) {
		this.unidadesTrabalho = unidadesTrabalho;
	}

	public List<NivelPagamento> getNiveisPagamento() {
		return niveisPagamento;
	}

	public void setNiveisPagamento(List<NivelPagamento> niveisPagamento) {
		this.niveisPagamento = niveisPagamento;
	}

	public List<CargaHorariaPagamento> getCargasHorariaPagamento() {
		return cargasHorariaPagamento;
	}

	public void setCargasHorariaPagamento(List<CargaHorariaPagamento> cargasHorariaPagamento) {
		this.cargasHorariaPagamento = cargasHorariaPagamento;
	}
	
}
