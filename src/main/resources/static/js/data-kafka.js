$(document).ready(function() {

	var sse = $.SSE('/kafka-messages', {
		onMessage: function(e) {

			var obj = JSON.parse(e.data);
			var mydata = { "fecha": obj.FECHA, "nombre": obj.NOMBRE, "followers": obj.FOLLOWERS, "perfil": "<div><a href=\"https://www.twitter.com/" + obj.USUARIO + "\" target=\"_blank\"><img src=\"" + obj.IMAGEN + "\"></img></a></div><div><a href=\"https://www.twitter.com/" + obj.USUARIO + "\" target=\"_blank\">@" + obj.USUARIO + "</a></div>", "tweet": obj.TEXTO };
			var dataa = [];
			dataa.push(mydata);
			$('#kafka-table').bootstrapTable('append', dataa);
			$('#kafka-table').bootstrapTable('scrollTo', 'bottom');


		},
		onError: function(e) {
			sse.stop();
			console.log("Could not connect..Stopping SSE");
		},
		onEnd: function(e) {
			console.log("End");
		}
	});
	sse.start();

});