
$(document).ready(function(){
    $.getJSON("../api/leaderboard", function(leaderboardJSON) {
        printLeaderboard (leaderboardJSON);
    });

    $.getJSON("../api/games", function(gamesJSON) {
        printMainGameList (gamesJSON);
    });
});

function printLeaderboard (lb){
	console.log(lb);

    $("#leaderboard").append("<tr>\
		<th class='bg'>Player ID</th>\
		<th>Name</th>\
		<th>Total Points</th>\
		<th>Won</th>\
		<th>Lost</th>\
		<th>Tied</th></tr>tr>");

    lb.sort((a, b) => b.results.sumOfPoints - a.results.sumOfPoints);
    lb.forEach((player) => {
        player.noOfmatches = player.results.won + player.results.lost + player.results.tied;
	});

    lb.forEach((player) => {
    	if (player.noOfmatches > 0){
            $("#leaderboard").append("<tr>\
				<td class='bg'>" + player.playerId + "</td>\
				<td>" + player.userName + "</td>\
				<td class='bg'>" + player.results.sumOfPoints + "</td>\
				<td>" + player.results.won + "</td>\
				<td>" + player.results.lost + "</td>\
				<td>" + player.results.tied + "</td></tr>tr>");
		}
	});
}

function printMainGameList (games){
	console.log(games);

	games.forEach((game) => {
		let creationDate = new Date(game.created);
		let playerTwo;
		let scoresResult;
		let scores;

		(game.gamePlayers.length < 2) ?
			playerTwo = "N/A" :
			playerTwo = game.gamePlayers[1].player.email;

        if (game.scores != undefined) {
            let scoreP0 = game.scores[0].score;
            let scoreP1 = game.scores[1].score;
			if (scoreP0 > scoreP1 && game.scores[0].playerId == game.gamePlayers[0].player.id){
                scoresResult = game.gamePlayers[0].player.email;
			} else if (scoreP0 > scoreP1 && game.scores[0].playerId == game.gamePlayers[1].player.id) {
                scoresResult = game.gamePlayers[1].player.email;
            } else if (scoreP0 == scoreP1){
                scoresResult = 'tie';
			} else if (scoreP0 < scoreP1 && game.scores[1].playerId == game.gamePlayers[0].player.id){
                scoresResult = game.gamePlayers[0].player.email;
			} else {
                scoresResult = game.gamePlayers[1].player.email;
			}

            scores = (scoresResult == 'tie') ?
				'Game finished. Result: tie.' : 'Game finished. Player ' + scoresResult + ' won.';
		} else {
        	scores = 'Game is not finished yet.';
		}

		$("#gameList").append("<li>ID: " + game.id + ", Created: " + creationDate + ",<br>\
			Player One: " + game.gamePlayers[0].player.email + ",<br>\
			Player Two: " 	+ playerTwo + "<br>" +
			scores + "<br><br></li>");
	});
}