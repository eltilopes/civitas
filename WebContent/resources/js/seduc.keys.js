;(function($) {

	var  charCode = null,
	     keys = {    8 : 'backspace',
					 9 : 'tab',
					13 : 'enter',
					16 : 'shift',
					17 : 'control',
					18 : 'alt',
					27 : 'esc',
					33 : 'page up',
					34 : 'page down',
					35 : 'end',
					36 : 'home',
					37 : 'left',
					38 : 'up',
					39 : 'right',
					40 : 'down',
					45 : 'insert',
					46 : 'delete',
					116 : 'f5',
					123 : 'f12',
					224 : 'command'
				},
		 masks = {
			'palavra' : /[a-zçáàãâéèêíìóòôõúùü\s]/i,
			'login'   : /[a-zçáàãâéèêíìóòôõúùü\s\.\-_\/]/i,
			'email'   : /[a-z0-9@\._\-]/i
		 },
		 
		 __getKeyNumber = function(e){
			 return (e.charCode || e.keyCode || e.which);
		 },
		 
		 __onKeyDown = function(e){
			 charCode = __getKeyNumber(e);
		 },
		 
		 __getPattern = function(obj) {
			 
			 var regex = null;
			  
			 if(typeof(obj) === "object") {
				 regex = new RegExp(obj.mask, "i");
			 } else if(typeof (obj) === "string") {
				 regex = masks[obj];
			 }
			 
			 return regex;
		 },
		 
		 __normalize = function(event) {
			 
			 if(__getKeyNumber(event) != 32) {
				 return;
			 }
			 
			 var el = $(this),
			 	 cursorPosition = parseInt(el.getCursorPosition()),
				 index = 0;
			
			if(cursorPosition > 0) {
				index = cursorPosition - 1;
			}
				
			var isValid = true;
			
			if(cursorPosition == 0) {
				isValid = false;
			} else {
				
				var lastCharCode = el.val().charCodeAt(index);
				isValid = (lastCharCode != 32);
				
				if(el.val().charAt(cursorPosition) !== "") {
					var nextCharCode = el.val().charCodeAt(cursorPosition);
					isValid = isValid && (nextCharCode != 32);
				}
			}
			
			if(!isValid) {
				event.preventDefault();
			}
		 },
		 
		 __onKeyPress = function(event) {
			 
			 var obj = event.data.mask;
			 
			 if(keys[charCode] !== undefined) {
			 	return;
			 }
			  
			  var regex = __getPattern(obj);
			  
			  if(!regex.test(String.fromCharCode(__getKeyNumber(event)))) {
				  event.preventDefault();
			  } else {
				  __normalize.call(this, event);
			  }
		 },
		 
		 /**
		  *  Remove os caracteres nao permitidos...
		  */
		 __fix = function(event){
			 
			 var $el = jQuery(this),
		     regex = __getPattern(event.data.mask);
		 
		    setTimeout(function(){
			 
				var palavra = $el.val(),
				     result = "";
				 
				while(regex.test(palavra)) {    
				    result = result.concat(regex.exec(palavra)[0]);
				    palavra = palavra.replace(regex, '');
				}
				 
				$el.val(result);
		    }, 1);
		 },
		 
		 __onPaste = function(event) {
			 __fix.call(this, event);
		 },
		 
		 __onChange = function(e) {
			 
			 __fix.call(this, e);
			 
			 var input = $(this).val();
				
			 input = $.trim(input);
			 input = input.replace(/\s{1,}/g, " ");
			
			 $(this).val(input);
		 };
	
	$.fn.applyMask = function(mask) {
		
		var mascara = { "mask": mask };
		
		this.bind("keydown",  mascara, __onKeyDown);
		this.bind("keypress", mascara, __onKeyPress);
		this.bind("change",   mascara, __onChange);
		this.bind("paste",    mascara, __onPaste);
	};
	
	$.fn.getCursorPosition = function() {
        var el = $(this).get(0);
        var pos = 0;
        if('selectionStart' in el) {
            pos = el.selectionStart;
        } else if('selection' in document) {
            el.focus();
            var Sel = document.selection.createRange();
            var SelLength = document.selection.createRange().text.length;
            Sel.moveStart('character', -el.value.length);
            pos = Sel.text.length - SelLength;
        }
        return pos;
    };
	
})(jQuery);
