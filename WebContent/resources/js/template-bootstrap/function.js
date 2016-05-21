// JavaScript Document

function entrar(){
	var cpf,nasc;
	cpf		= $("input[name='cpf']").val();
	nasc	= $("input[name='nascimento']").val();
	
	//ADD ATRIBUTOS
	if(cpf == "" || nasc == "" || cpf.length == 0 || cpf == '000.000.000-00' || nasc == '00/00/0000' ){
		erroAndAlert("Dados","Por favor, insira os dados corretamente.");	
		return false;
	}else{
		//TENTAR LOGAR
		$.ajax({
		url: "login.json",
		data: {cpf: cpf,nascimento:nasc},
		type : 'post',
	    dataType : 'json',
		async: false,
		success : function(response){
				if(response["codigo"] == 0){
					//LOGIN SUCESSO!!
					alert();
					location.href	= 'conteudo.html';					
				}else {
					erroAndAlert(response["title"],response["alert"]);
				}
			}
		});
			
	}
	
	
	
	
	}
	

/* LOADING FUNCTION */
function ProcessandoModalMod(titulo){
		$('#ModalProcessandoTitle').html(titulo);
		$("#ModalProcessando").modal().showModalDialog();
	}
	

function confirma(titulo,conteudo,confirmObj){
	//alert(confirmObj.msgConfig);
	$('#myModalLabel').html(titulo);
	
	if(conteudo == false){
	$('#ModalTitle').html("");
	}else{
	$('#ModalTitle').html(conteudo);
	}
	
	//CALLBACK
	$('#VerdadeiroBtn').html(confirmObj.btn_value);
	$('#VerdadeiroBtn').show();
	$('#myModal').modal().showModalDialog();
	
	}


function erroAndAlert(titulo,conteudo){
	$('#myModalLabel').html(titulo);
	$('#ModalTitle').html(conteudo);
	
	$('#VerdadeiroBtn').hide();
	
	//falsoBtn
	$('#myModal').modal().showModalDialog();
	}
	

	