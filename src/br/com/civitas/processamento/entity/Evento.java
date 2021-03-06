package br.com.civitas.processamento.entity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.civitas.arquitetura.entity.IEntity;
import br.com.civitas.processamento.enums.TipoArquivo;
import br.com.civitas.processamento.enums.TipoEvento;

@Entity
@Table(name = "tb_evento")
public class Evento implements IEntity {

	private static final long serialVersionUID = -176970653069185670L;

	@Id
	@GeneratedValue(generator = "seq_evento", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_evento", sequenceName = "seq_evento", allocationSize = 1)
	@Column(name = "ci_evento")
	private Long id;

	@Column(name = "nm_nome", nullable = false)
	private String nome;

	@Column(name = "nm_chave", nullable = false)
	private String chave;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cd_cidade", nullable = false)
	private Cidade cidade;

	@Column(name = "nr_tipo_arquivo", nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private TipoArquivo tipoArquivo;

	@Column(name = "nr_tipo_evento",nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private TipoEvento tipoEvento  = TipoEvento.NENHUM;
	
	public Evento() {
	}

	public Evento(String chave) {
		this.chave = chave;
		this.nome = chave;
	}

	public Evento(String chave, Cidade cidade, TipoArquivo tipoArquivo) {
		this.chave = chave;
		this.nome = chave;
		this.cidade = cidade;
		this.tipoArquivo = tipoArquivo;
	}
	
	public Evento(Long id, String chave, String nome, Cidade cidade, TipoArquivo tipoArquivo) {
		this.id = id;
		this.chave = chave;
		this.nome = nome;
		this.cidade = cidade;
		this.tipoArquivo = tipoArquivo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public Map<String, Object> notEmptyFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		if(id != null ){
			map.put("id", id);
		}if(cidade != null && cidade.getId() != null ){
			map.put("cidade.id",cidade.getId() );
		}if(tipoArquivo != null  ){
			map.put("tipoArquivo",tipoArquivo.getCodigo() );
		}if(tipoEvento != null  ){
			map.put("tipoEvento",tipoEvento.getCodigo() );
		}
		return map;
	}	

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}

	public TipoArquivo getTipoArquivo() {
		return tipoArquivo;
	}

	public void setTipoArquivo(TipoArquivo tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public TipoEvento getTipoEvento() {
		return tipoEvento;
	}

	public void setTipoEvento(TipoEvento tipoEvento) {
		this.tipoEvento = tipoEvento;
	}

	@Override
	public String toString() {
		return nome;
	}
}
