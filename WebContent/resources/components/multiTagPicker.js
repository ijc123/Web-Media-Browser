/**
 * 
 */
if(!multiTagPicker) var multiTagPicker = {

	RemoveSelectedTag: function(element, clientId, tag) {
	
		var hiddenSelectedTagsId =  clientId + ':' + 'hiddenSelectedTags';	
		var hiddenSelectedTags = document.getElementById(hiddenSelectedTagsId);
		
		//var arr = ListStringToArray(hiddenSelectedTags.value);
		var arr = jQuery.parseJSON(hiddenSelectedTags.value);
	
		var pos = jQuery.inArray(tag, arr);
		
		if(pos == -1) return;
		
		arr.splice(pos, 1);
		
		hiddenSelectedTags.value = jQuery.toJSON(arr);
		$(element.parentNode).remove();
		
	},

	AddSelectedTag: function(clientId, tag) {
	
		// grab the current selectedTags string for this tag from the hidden input field and convert it to a array
		var hiddenSelectedTagsId =  clientId + ':' + 'hiddenSelectedTags';	
		var hiddenSelectedTags = document.getElementById(hiddenSelectedTagsId);
		
		//var arr = ListStringToArray(hiddenSelectedTags.value);
		var arr = jQuery.parseJSON(hiddenSelectedTags.value);
	
		// return and do nothing if tag is already used
		if(jQuery.inArray(tag, arr) != -1) return;
		
		// add the tag to the list and the hidden input field
		var selectedTagsId  = clientId + ':' + 'selectedList';
		var newListItem = document.createElement('li');
		
		var listItemLink = document.createElement('a');
		listItemLink.href = "#";
		listItemLink.onclick = function dummy() {
			
			multiTagPicker.RemoveSelectedTag(this, clientId, tag);
			return(false);
		}; 
		
		 listItemLink.innerHTML = tag;
		 
		 newListItem.appendChild(listItemLink);
		
		arr = arr.concat(tag);
		
		hiddenSelectedTags.value = jQuery.toJSON(arr);
		
		$(document.getElementById(selectedTagsId)).append(newListItem);
	
	}

};