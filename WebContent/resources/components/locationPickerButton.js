if(!locationPickerButton) var locationPickerButton = {

	Redraw: function(clientId) {
		
		clientId = clientId + ':mediaLocation';
		
		jsf.ajax.request(clientId, null, {render: clientId});
	}
};