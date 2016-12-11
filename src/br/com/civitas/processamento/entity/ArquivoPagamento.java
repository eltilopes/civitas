package br.com.civitas.processamento.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.primefaces.model.UploadedFile;

import br.com.civitas.arquitetura.entity.IEntity;
import br.com.civitas.processamento.enums.TipoArquivo;


@Entity
@Table(name = "tb_arquivo_pagamento")
public class ArquivoPagamento implements IEntity {

	private static final long serialVersionUID = 7508490594204603372L;

	@Id
	@GeneratedValue(generator = "seq_arquivo_pagamento", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_arquivo_pagamento", sequenceName = "seq_arquivo_pagamento", allocationSize = 1)
	@Column(name = "ci_arquivo_pagamento")
	private Long id;
	
	@Column(name = "nm_arquivo",nullable = false, length = 30)
	private String nomeArquivo;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cd_cidade", nullable = false)
	private Cidade cidade;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cd_ano", nullable = false)
	private Ano ano;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cd_mes", nullable = false)
	private Mes mes;
	
	@Column(name = "nr_tipo_arquivo",nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private TipoArquivo tipoArquivo;
	
	@Column(name = "nr_total_pagamentos",nullable = false)
	private int totalPagamentos;

	@Column(name = "nr_total_proventos",nullable = false)
	private Double totalProventos;

	@Column(name = "nr_total_descontos",nullable = false)
	private Double totalDescontos;

	@Column(name = "nr_total_liquido",nullable = false)
	private Double totalLiquido;

	@Column(name = "nr_total_remuneracao",nullable = false)
	private Double totalRemuneracao;

	@Column(name = "dt_processamento", nullable=false)
	@Temporal(TemporalType.DATE)
	private Date dataProcessamento;
	
	@Transient
	private int totalSetores;
	
	@Transient
	private UploadedFile file;
	
	public ArquivoPagamento(){
		totalDescontos = 0D;
		totalLiquido = 0D;
		totalRemuneracao = 0D;
		totalProventos = 0D;
		totalPagamentos = 0;
		dataProcessamento = new Date();
	}

	public ArquivoPagamento(String nomeArquivo){
		this.nomeArquivo = nomeArquivo;
		totalDescontos = 0D;
		totalLiquido = 0D;
		totalRemuneracao = 0D;
		totalProventos = 0D;
		totalPagamentos = 0;
		dataProcessamento = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}

	public Ano getAno() {
		return ano;
	}

	public void setAno(Ano ano) {
		this.ano = ano;
	}

	public Mes getMes() {
		return mes;
	}

	public void setMes(Mes mes) {
		this.mes = mes;
	}

	public TipoArquivo getTipoArquivo() {
		return tipoArquivo;
	}

	public void setTipoArquivo(TipoArquivo tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public int getTotalPagamentos() {
		return totalPagamentos;
	}

	public void setTotalPagamentos(int totalPagamentos) {
		this.totalPagamentos = totalPagamentos;
	}

	public Double getTotalProventos() {
		return totalProventos;
	}

	public void setTotalProventos(Double totalProventos) {
		this.totalProventos = Double.valueOf(String.format(Locale.US, "%.2f", totalProventos));
	}

	public Double getTotalDescontos() {
		return totalDescontos;
	}

	public void setTotalDescontos(Double totalDescontos) {
		this.totalDescontos = Double.valueOf(String.format(Locale.US, "%.2f", totalDescontos));
	}

	public Double getTotalLiquido() {
		return totalLiquido;
	}

	public void setTotalLiquido(Double totalLiquido) {
		this.totalLiquido = Double.valueOf(String.format(Locale.US, "%.2f", totalLiquido));
	}

	public Double getTotalRemuneracao() {
		return totalRemuneracao;
	}

	public void setTotalRemuneracao(Double totalRemuneracao) {
		this.totalRemuneracao = Double.valueOf(String.format(Locale.US, "%.2f", totalRemuneracao));
	}

	public Date getDataProcessamento() {
		return dataProcessamento;
	}

	public void setDataProcessamento(Date dataProcessamento) {
		this.dataProcessamento = dataProcessamento;
	}
	
	public int getTotalSetores() {
		return totalSetores;
	}

	public void setTotalSetores(int totalSetores) {
		this.totalSetores = totalSetores;
	}
	
	@Override
	public Map<String, Object> notEmptyFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		if(id != null ){
			map.put("id", id);
		}if(cidade != null && cidade.getId() != null ){
			map.put("cidade.id",cidade.getId() );
		}if(ano != null && ano.getId() != null ){
			map.put("ano.id",ano.getId() );
		}if(mes != null && mes.getId() != null ){
			map.put("mes.id",mes.getId() );
		}if(tipoArquivo != null  ){
			map.put("tipoArquivo",tipoArquivo.getCodigo() );
		}
		return map;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArquivoPagamento other = (ArquivoPagamento) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

}
