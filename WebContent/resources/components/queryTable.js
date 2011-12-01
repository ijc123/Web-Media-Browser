/**
 * 
 */
/**
 * 
 */

var isChecked = false;

function toJQueryId(myid) { 
	return '#' + myid.replace(/(:|\.)/g,'\\$1');
}


function toggleSelectAll(id)
{
	
	// escape ':' characters in the jsf id to make them work with jquery
	var tableId = toJQueryId(id + ":table");
	
	if(isChecked == false) {
		
		 isChecked = true;
		
	} else {
		 
		isChecked = false;
	}
	
	$(tableId + ' input[type=checkbox]').attr('checked',isChecked);

 	return;
}