/**
 * 
 */
if(!pager) var pager = {
		
		SetToZero: function() {
			
			// find the media player(s) with id matching playerId
			var query = "input[id*=pager]:hidden";
					
			$(query).each(function() {
									
				this.value = '0';
				
			});
			
		}
		
};