var imageSearch;

var contentDivID = "createTagForm:content";

function addPaginationLinks() {

	// To paginate search results, use the cursor function.
	var cursor = imageSearch.cursor;
	var curPage = cursor.currentPageIndex; // check what page the app is on
	var pagesDiv = document.createElement('div');
	
	for(var i = 0; i < cursor.pages.length; i++) {
		
		var page = cursor.pages[i];
		
		if (curPage == i) {

			// If we are on the current page, then don't make a link.
			var label = document.createTextNode(' ' + page.label + ' ');
			pagesDiv.appendChild(label);
			
		} else {

			// Create links to other pages using gotoPage() on the searcher.
			var link = document.createElement('a');
			link.href = 'javascript:imageSearch.gotoPage(' + i + ');';
			link.innerHTML = page.label;
			link.style.marginRight = '2px';
			pagesDiv.appendChild(link);
		}
	}

	var contentDiv = document.getElementById(contentDivID);
	contentDiv.appendChild(pagesDiv);
}

function searchComplete() {

	// Check that we got results
	if (imageSearch.results && imageSearch.results.length > 0) {

		// Grab our content div, clear it.
		var contentDiv = document.getElementById(contentDivID);
		contentDiv.innerHTML = '';

		// Loop through our results, printing them to the page.
		var results = imageSearch.results;
		for ( var i = 0; i < results.length; i++) {
			// For each result write it's title and image to the screen
			var result = results[i];
			var radioSelect = document.createElement('input');
			radioSelect.type = 'radio';
			radioSelect.value = result.url;
			radioSelect.name = 'selectImage';
			radioSelect.onclick = function stupid(event) {
				// Find which element was acted upon (DOM2 and IE branching)
				target = event ? event.target : (window.event ? window.event.srcElement : null);
				// Call function with the parameter associated with event target
				if(target) {
					
					// get the jsf id of the parent form using jquery
					var formId = $(this).parents('form:first')[0][0].name;
					// show the selected tag image url
					var tagImageURLId = formId + ':' + 'tagImageURL';
			
					var output = document.getElementById(tagImageURLId);
					output.value = this.value;
					
					// show the selected tag image
					var tagImageId = formId + ':' + 'tagImage';
					
					output = document.getElementById(tagImageId);
					output.src = this.value;
					
				}
			};

			// We use titleNoFormatting so that no HTML tags are left in the
			// title
			var newImg = document.createElement('img');

			// There is also a result.url property which has the escaped version
			newImg.src = result.tbUrl;

			// imgContainer.appendChild(radioSelect);
			// imgContainer.appendChild(newImg);

			// Put our title + image in the content
			// contentDiv.appendChild(imgContainer);

			contentDiv.appendChild(radioSelect);
			contentDiv.appendChild(newImg);
			

		}

		// Now add links to additional pages of search results.
		addPaginationLinks(imageSearch);
	}
}

function OnLoad(query) {

	// Create an Image Search instance.
	imageSearch = new google.search.ImageSearch();
	imageSearch.setRestriction(google.search.Search.RESTRICT_SAFESEARCH,
			google.search.Search.SAFESEARCH_OFF);

	// Set searchComplete as the callback function when a search is
	// complete. The imageSearch object will have results in it.
	imageSearch.setSearchCompleteCallback(this, searchComplete, null);

	// Find me a beautiful car.
	imageSearch.execute(query);

	// Include the required Google branding
	//google.search.Search.getBranding('branding');
}

function DoQuery(event, element) {

	var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
	
	if (keyCode == 13) {
				
		OnLoad(element.value);
	} 
}

function ShowTagImage(element, event) {
	
	var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
	
	if (keyCode != 13) return;

	// get the jsf id of the parent form using jquery
	var formId = $(element).parents('form:first')[0][0].name;
	// show the selected tag image
	var tagImageId  = formId + ':' + 'tagImage';
	
	output = document.getElementById(tagImageId);
	output.src = element.value;
	
}


