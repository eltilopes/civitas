function Lotacao(){};

Lotacao.prototype.msgValidacao = function(){
	if(jQuery('.ui-messages-error').html()!=null){ 
		jQuery('html, body').animate({scrollTop:0}, 'slow');
	}
};

Lotacao.prototype.somenteNumero = function(e){
	var tecla = (window.event) ? event.keyCode : e.which;
	if ((tecla > 47 && tecla < 58))
		return true;
	else {
		if (tecla == 8 || tecla == 0)
			return true;
		else
			return false;
	}
};

var lotacao = new Lotacao(); 

//jQuery(window).scroll(function() {
//	if(scrollY>=200){ 
//		jQuery("#boxSubstituto").attr('style','position: fixed;background: #fff; z-index: 10000;width: 936px;top:0'); 
//	}else{
//		jQuery("#boxSubstituto").attr('style','');
//	}
//});

jQuery(".validacao").click(function(){
	lotacao.msgValidacao();
});
