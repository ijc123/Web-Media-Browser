/**
 * 
 */
   function setSelectedDirectory(form)
    {
        var select = form.elements["selectFile"];
        var itemText = select.options[select.selectedIndex].text;
    	var output = form.elements["fullPath"];
    	output.value = itemText; 
     	return;
    }