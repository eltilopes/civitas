jQuery(function($) {
	$('.panel-heading span.clickable').on( "click", function(e) {
		if ($(this).hasClass('panel-collapsed')) {
			// expand the panel
			$(this).parents('.panel').find('.panel-body').slideDown();
			$(this).removeClass('panel-collapsed');
			$(this).find('i').removeClass('glyphicon-chevron-down')
					.addClass('glyphicon-chevron-up');
		} else {
			// collapse the panel
			$(this).parents('.panel').find('.panel-body').slideUp();
			$(this).addClass('panel-collapsed');
			$(this).find('i').removeClass('glyphicon-chevron-up')
					.addClass('glyphicon-chevron-down');
		}
	});
	
	$('.panel-heading h5.clickable').on( "click", function(e) {
		if ($('.panel-heading span.clickable').hasClass('panel-collapsed')) {
			// expand the panel
			$('.panel-heading span.clickable').parents('.panel').find('.panel-body').slideDown();
			$('.panel-heading span.clickable').removeClass('panel-collapsed');
			$('.panel-heading span.clickable').find('i').removeClass('glyphicon-chevron-down')
					.addClass('glyphicon-chevron-up');
		} else {
			// collapse the panel
			$('.panel-heading span.clickable').parents('.panel').find('.panel-body').slideUp();
			$('.panel-heading span.clickable').addClass('panel-collapsed');
			$('.panel-heading span.clickable').find('i').removeClass('glyphicon-chevron-up')
					.addClass('glyphicon-chevron-down');
		}
	});
});