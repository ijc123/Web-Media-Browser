/**
 * 
 */
function setDefaultRenameText() {
	
	var inputElem = document.getElementById('uploadFile');
	var renameElem = document.getElementById('renameFile');

	var fileName = inputElem.value;
	var index = fileName.lastIndexOf("/");
	if(index == -1)  index = fileName.lastIndexOf("\\");

	if(index != -1) {

		fileName = fileName.substring(index + 1, fileName.length);
			
	}  

	renameElem.value = fileName;

}
