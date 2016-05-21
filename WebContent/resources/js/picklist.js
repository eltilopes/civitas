var Item = function(numero, nome) {
	this.numero  = numero;
	this.nome    = nome;
};

Item.prototype.sort = function(item1, item2) {
	var nome1 = item1.nome, nome2 = item2.nome;

	return (nome1 > nome2) ? (1) : (nome1 < nome2 ? -1 : 0);
};

Item.prototype.toString = function() {
    return 'numero: ' + this.numero + ' -- nome: ' + this.nome;
};

var parse = function(lista, holder) {
	
	var items = [], 
	    values = holder.val().split(',');

	lista.children('.ui-picklist-item').each(function(i, item) {
	   items.push(new Item(values[i], jQuery(this).text()));
	});

	items.sort(Item.prototype.sort);
	
	var hash = [];
	lista.children('.ui-picklist-item').each(function(index) {
		
		jQuery(this).text(items[index].nome);
		hash.push(items[index].numero);
	});
	
	var itemValues = hash,
    itemElements = lista.children('li');

    for(var i in itemValues) {
       jQuery(itemElements.get(i)).data('itemValue', itemValues[i]);
    }
    
    holder.val(hash.join(','));
};

if(typeof PrimeFaces !== 'undefined' ) {

	/* 
	   Monkey patching para ordernar as lista(source e target) do picklist
	 */
	PrimeFaces.widget.PickList.prototype.saveState = function() {
		
		this.cfg.effectSpeed = -1;
		
		this.saveListState(this.sourceList, this.sourceState);
		this.saveListState(this.targetList, this.targetState);
	
		parse(this.sourceList, this.sourceState);
		parse(this.targetList, this.targetState);
	};

	PrimeFaces.widget.PickList.prototype.iterate = function(lista, holder, callback) {
		
		var itemValues = holder.val().split(',');
			itemElements = lista.children('li');
			
		for(var i in itemValues) {
			callback.call(this, jQuery(itemElements.get(i)));
		}
	};
}