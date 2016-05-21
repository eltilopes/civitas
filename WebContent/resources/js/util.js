/**
 * 
 */
jQuery(document).ready( function() {
	attachEventDelete();
});

var attachEventDelete = function() {
	jQuery(".ui-checkBox").bind('change', showDelete );
	jQuery(".ui-checkBoxMain").bind('change',  checkboxAll );
	   
	clearCheckbox();
};

function checkboxAll() {
	jQuery(".botaoExcluir").toggle(this.checked);
	jQuery(".ui-checkBox").prop('checked', this.checked);
}

function showIf() {
	if(isCheckedBox()) 
	   panel.show();
}

function showDelete() {
	jQuery(".botaoExcluir").toggle(isCheckedBox());	
	verificarTodosCheckbox();
}

function isCheckedBox(){
	return jQuery('.ui-checkBox:checked').length > 0;
}

function verificarTodosCheckbox() {
	var bool = true;
	jQuery(".ui-checkBox").each(
	   function() {
	      bool = bool && this.checked;	
	});
	
	if( jQuery(".ui-checkBox").length === 0 ) {
		bool = false;
	}
	
	var proxy = jQuery(".ui-checkBoxMain");

	proxy.removeAttr("checked");
	if (bool)
		proxy.attr("checked", "checked");
}

function clearCheckbox() {
	jQuery(".ui-checkBox").removeAttr("checked");
	jQuery(".ui-checkBoxMain").removeAttr("checked");
}

if(typeof YAHOO !== 'undefined' ) {
	
	YAHOO.widget.Paginator.prototype.fireBeforeChangeEvent = function() {
		attachEventDelete();
		
		var $checkBoxMaster =  jQuery(".ui-checkBoxMain");
		if(jQuery(".ui-checkBox").length === 0) {
			$checkBoxMaster.hide();
		} else {
			$checkBoxMaster.show();
		}
	};
}

PrimeFaces.locales['pt'] = {
	    closeText: 'Fechar',
	    prevText: 'Anterior',
	    nextText: 'Próximo',
	    currentText: 'Começo',
	    monthNames: ['Janeiro','Fevereiro','Março','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'],
	    monthNamesShort: ['Jan','Fev','Mar','Abr','Mai','Jun', 'Jul','Ago','Set','Out','Nov','Dez'],
	    dayNames: ['Domingo','Segunda','Terça','Quarta','Quinta','Sexta','Sábado'],
	    dayNamesShort: ['Dom','Seg','Ter','Qua','Qui','Sex','Sáb'],
	    dayNamesMin: ['D','S','T','Q','Q','S','S'],
	    weekHeader: 'Semana',
	    firstDay: 0,
	    isRTL: false,
	    showMonthAfterYear: false,
	    yearSuffix: '',
	    timeOnlyTitle: 'Só Horas',
	    timeText: 'Tempo',
	    hourText: 'Hora',
	    minuteText: 'Minuto',
	    secondText: 'Segundo',
	    ampm: false,
	    month: 'Mês',
	    week: 'Semana',
	    day: 'Dia',
	    allDayText : 'Todo o Dia'
	};