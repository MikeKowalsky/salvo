
$(document).ready(function(){

    $('#logout-form').hide();
    getLeaderboardJSON();
    getGamesJSON();

});

function getLeaderboardJSON(){
    $.getJSON("../api/leaderboard", function(leaderboardJSON) {
        printLeaderboard (leaderboardJSON);
    });
}

function getGamesJSON() {
    $.getJSON("../api/games", function(gamesJSON) {
        console.log(gamesJSON);

        if (gamesJSON.player != null){
            cleanMainGameList();
            printMainGameList (gamesJSON);
            printUserName(gamesJSON.player);
        } else {
            printMainGameList (gamesJSON);
        }

    });
}

function login(evt) {
    evt.preventDefault();
    let form = evt.target;
    $.post("/api/login",
            { name: form["name"].value,
             pwd: form["pwd"].value })
        .done(function() {
            console.log("logged in!");
            getGamesJSON();
            $('#logout-form').show();
        });

}

function logout(evt) {
    // evt.preventDefault();
    $.post("/api/logout")
        .done(function() { console.log("logged out"); });
}

function signin(evt) {
    evt.preventDefault();
    let form = evt.target;
    $.post("/api/players",
        {username: form["name"].value,
        password: form["pwd"].value})
        .done(function () {
            console.log("new player created");
            getLeaderboardJSON();
        });
}

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

function cleanMainGameList(){
    $('#gameList').empty();
}

function printUserName(player) {
    $('#login-form').hide();
    $('#signin-form').hide();
    $('#userName').append('<div>User name: ' + player.email + '</div>')
}

function printMainGameList (games){
	console.log(games);

	games.games.forEach((game) => {
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