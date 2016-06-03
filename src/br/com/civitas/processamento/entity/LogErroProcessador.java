package br.com.civitas.processamento.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.civitas.arquitetura.entity.IEntity;

@Entity
@Table(name = "tb_log_erro_processador")
public class LogErroProcessador implements IEntity  {

	private static final long serialVersionUID = -948343171929221042L;

	@Id
	@GeneratedValue(generator = "seq_log_erro_processador", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_log_erro_processador", sequenceName = "seq_log_erro_processador", allocationSize = 1)
	@Column(name = "ci_log_erro_processador")
	private Long id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_data", nullable = false)
	private Date data;

	@Column(name = "nm_arquivo", nullable = false)
	private String nomeArquivo;

	@Column(name = "ds_erro", nullable = false)
	private String erro;

	public LogErroProcessador() {
		this.data = new Date();
	}
	
	public LogErroProcessador(String nomeArquivo, String erro) {
		this.erro = erro;
		this.nomeArquivo = nomeArquivo;
		this.data = new Date();
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getErro() {
		return erro;
	}

	public void setErro(String erro) {
		this.erro = erro;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Map<String, Object> notEmptyFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		if(id != null ){
			map.put("id", id);
		}if(nomeArquivo != null && nomeArquivo != "" ){
			map.put("nomeArquivo",nomeArquivo);
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
		LogErroProcessador other = (LogErroProcessador) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}