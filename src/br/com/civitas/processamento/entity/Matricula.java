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
import javax.persistence.Transient;

import br.com.civitas.arquitetura.entity.IEntity;

@Entity
@Table(name = "tb_matricula")
public class Matricula implements IEntity {
	
	private static final long serialVersionUID = 7550775310790609268L;
	
	@Id
	@GeneratedValue(generator = "seq_matricula", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_matricula", sequenceName = "seq_matricula", allocationSize = 1)
	@Column(name = "ci_matricula")
	private Long id;
	  
	@Transient
	private MatriculaPagamento matriculaPagamento;
	
	@Column(name = "nr_matricula",nullable=false, length=9)
	private String numeroMatricula;
	
	@Column(name = "nm_funcionario",nullable=false)
	private String nomeFuncionario;

	@Column(name = "dt_admissao")
	@Temporal(TemporalType.DATE)
	private Date dataAdmissao;

	@Transient
	private boolean selecionado;
	
	public Matricula() {}

	public Matricula(String numeroMatricula) {
		this.numeroMatricula = numeroMatricula;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumeroMatricula() {
		return numeroMatricula;
	}

	public void setNumeroMatricula(String numeroMatricula) {
		this.numeroMatricula = numeroMatricula;
	}

	public Date getDataAdmissao() {
		return dataAdmissao;
	}

	public void setDataAdmissao(Date dataAdmissao) {
		this.dataAdmissao = dataAdmissao;
	}

	public String getNomeFuncionario() {
		return nomeFuncionario;
	}

	public void setNomeFuncionario(String nomeFuncionario) {
		this.nomeFuncionario = nomeFuncionario;
	}

	public MatriculaPagamento getMatriculaPagamento() {
		return matriculaPagamento;
	}

	public void setMatriculaPagamento(MatriculaPagamento matriculaPagamento) {
		this.matriculaPagamento = matriculaPagamento;
	}

	@Override
	public Map<String, Object> notEmptyFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		if(id != null ){
			map.put("id", id);
		}if(nomeFuncionario != null && nomeFuncionario != ""){
			map.put("nomeFuncionario", nomeFuncionario);
		}
		return map;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

}
