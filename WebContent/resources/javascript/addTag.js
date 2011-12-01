/**
 * 
 */

if(!addTagJS) var addTagJS = {

		DoQuery: function(event, id) {
	
			// make sure the resulting datatable starts at page 0
			pager.SetToZero();
			
			var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
		
			if (keyCode == 13) {
				
				var source = 'addTagForm:query';
				
				//jsf.ajax.request(source, null, {execute: 'query calender', render: 'queryTable'});
				//jsf.ajax.request(source, null, {render: 'queryTable'});
				jsf.ajax.request(source, null, {execute: '@form', render: '@form'});
			} 
		}
};