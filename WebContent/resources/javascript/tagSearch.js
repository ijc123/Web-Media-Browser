/**
 * 
 */
if(!tagSearch) var tagSearch = {

	RemoveSelectedTag: function(clientId, tag) {
	
		// grab the current selectedTags string for this tag from the hidden input field and convert it to a array
		var queryTags = $("input[id*=queryTags]:hidden").get(0);
		
		//var arr = ListStringToArray(queryTags.value);
		var arr = jQuery.parseJSON(queryTags.value);
	
		var pos = jQuery.inArray(tag, arr);
		
		if(pos == -1) return;
		
		arr.splice(pos, 1);
		
		//queryTags.value = jQuery.toJSON(arr);
		queryTags.value = $.toJSON(arr);
			
		jsf.ajax.request(queryTags.id, null, {execute: queryTags.id, render: '@form'});
		
	},

	ClearSearchTags: function() {
		
		// grab the current selectedTags string for this tag from the hidden input field and convert it to a array
		var queryTags = $("input[id*=queryTags]:hidden").get(0);
				
		queryTags.value = "[]";
		
		jsf.ajax.request(queryTags.id, null, {execute: queryTags.id, render: '@form'});
			
	},
		
	AddSelectedTag: function(tag, selectedTab) {
	
		// grab the current selectedTags string for this tag from the hidden input field and convert it to a array
		
		var queryTags = $("input[id*=queryTags]:hidden").get(0);
		
		var arr = jQuery.parseJSON(queryTags.value);
	
		// return and do nothing if tag is already used
		if(jQuery.inArray(tag, arr) != -1) return;
						
		arr = arr.concat(tag);
		
		//queryTags.value = jQuery.toJSON(arr);
		queryTags.value = $.toJSON(arr);
			
		jsf.ajax.request(queryTags.id, null, {execute: queryTags.id, render: '@form', selectedTab: selectedTab});
	},
	

};