/*
 * JS criado para manipulação de 'tooltip' 
 * ícones de datatables.
 * 
 * A ação será replicada em cada ação feita na tabela:
 * Paginação, ordenação, etc.
 * 
 * author: Sérgio Danilo - 24/09/2015
 */

$(function(){
	$(".tool-tip").tooltip();
	
	$(".ui-paginator-page").load(function() {
		$(".tool-tip").tooltip();
	});
});

function showTooltip(){
	$(".tool-tip").tooltip();
}