/**
 * 
 */

var selectAllToggle = 0;

function toggleSelectAll(table)
{
		
	var tablebody = table.getElementsByTagName("tbody")[0];
	var row = tablebody.getElementsByTagName("tr");
	
	var isChecked = true;
	
	if(selectAllToggle == 1) {
		
		 isChecked = false;
		selectAllToggle = 0;
		
	} else {
		 
		selectAllToggle = 1;
	}
	
	// first row is the table header so start at 1
	for(var i = 1; i < row.length; i++) {
		
		var selectCel = row[i].getElementsByTagName("td")[0];
			
		selectCel.children[0].checked = isChecked;
		
	}
	
 	return;
}