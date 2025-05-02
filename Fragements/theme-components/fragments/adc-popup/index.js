$('[data-target="#chatbot-container"]').on('click',function(){
   			var x = document.getElementById("chatbot-container")
 				
				console.log("toggleChatbot()");
				if( $(x).children().length === 0 ) {
					initSdk('Bots');
					console.log("Init chatbot");
				}

   
 });