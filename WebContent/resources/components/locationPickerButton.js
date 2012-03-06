if(!locationPickerButton) var locationPickerButton = {

	RedrawButton: function(clientId) {
		
		clientId = clientId + ':mediaLocation';
		
		jsf.ajax.request(clientId, null, {render: clientId});
	}

};