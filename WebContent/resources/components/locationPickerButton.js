if(!locationPickerButton) var locationPickerButton = {

	RedrawButton: function(clientId) {
		
		clientId = clientId + ':mediaLocation';
		
		jsf.ajax.request(clientId, null, {render: clientId});
	},

	RedrawPopup: function(selectId, ftpId) {
		
		jsf.ajax.request(ftpId, null, {execute: ftpId, render: selectId});
	}
};