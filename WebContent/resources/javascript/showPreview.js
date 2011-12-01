/**
 * 
 */

if(!showPreview) var showPreview = {
		
		StartVideo: function(playerId, e, image, imageInfo) {
			
			// find the media player(s) with id matching playerId
			var query = "*[id*=" + playerId + "]";
			
			$(query).each(function() {
							
				if(this.type == "application/x-mplayer2" || this.classid == "CLSID:6BF52A52-394A-11d3-B153-00C04F79FAA6") {
							
					var pageX = 0;
					var pageY = 0;
										
					if( typeof( e.pageX ) == 'number' ) {	
						
					   pageX = e.pageX;
					   pageY = e.pageY;
						  
					} else if( typeof( e.clientX ) == 'number' ) {
										
					   pageX = e.clientX + document.documentElement.scrollLeft;
					   pageY = e.clientY + document.documentElement.scrollTop;
						  
					}
					
					var x = pageX - $(image).offset().left;
				    var y = pageY - $(image).offset().top;
								
				    var largeHeight = parseInt(imageInfo[1]);
				    
				    var smallWidth = parseInt(imageInfo[2]);
				    var smallHeight = parseInt(imageInfo[3]);
				    		    
				    var yOffset = largeHeight - Math.floor(largeHeight / smallHeight) * smallHeight;
				    
				    var col = Math.floor(x / smallWidth);				       
				    var row = Math.floor((y - yOffset) / smallHeight);
				    
				    var index = row * 2 + col;
				    
				    var timeOffsetSeconds = parseInt(imageInfo[index + 4]);
				    
				    if((y - yOffset) < 0) {
				    	timeOffsetSeconds = 0;
				    }
				    				    
					this.controls.currentPosition = timeOffsetSeconds;
					this.controls.play();
				}
			
			});
			
		},

		StopVideo: function(playerId) {
			
			var query = "*[id*=" + playerId + "]";
			
			$(query).each(function() {
				
				if(this.type == "application/x-mplayer2" || this.classid == "CLSID:6BF52A52-394A-11d3-B153-00C04F79FAA6") {
					
					this.controls.stop();
					
				}
				
			});
		}
		
};