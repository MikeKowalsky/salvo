
$.getJSON("../api/games", function(gamesJSON) {
        main (gamesJSON);
});

function main (games){
	console.log(games);



	games.forEach((game) => {
		let creationDate = new Date(game.created);
		let playerTwo;
		(game.gamePlayers.length < 2) ? 
				playerTwo = "N/A" : 
				playerTwo = game.gamePlayers[1].player.email;
		$("#gameList").append("<li>ID: " + game.id + ", Created: " + creationDate + ",<br>\
			Player One: " + game.gamePlayers[0].player.email + ",<br>\
			Player Two: " 	+ playerTwo + "<br><br></li>");
	});
}