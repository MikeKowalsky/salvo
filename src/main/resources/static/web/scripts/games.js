
$(document).ready(function(){

    getLeaderboardJSON();
    getGamesJSON();
    activateEventsListeners();

});

function getLeaderboardJSON(){
    $.getJSON("../api/leaderboard", function(leaderboardJSON) {
        cleanLeaderboard();
        printLeaderboard (leaderboardJSON);
    });
}

function getGamesJSON() {
    $.getJSON("../api/games", function(gamesJSON) {
        console.log(gamesJSON);

        if (isAnyUserLoggedIn(gamesJSON.player)){
            cleanMainGameList();
            printMainGameList (gamesJSON);
            printUserNameAndShowButtons(gamesJSON.player);
            $('#logout-form').show();
        } else {
            printMainGameList (gamesJSON);
            $('#logout-form').hide();
            $('#newGame').hide();
        }
    });
}

function login(form) {
    $.post("/api/login",
            { name: form[0]["name"].value,
             pwd: form[0]["pwd"].value })
        .done(function() {
            console.log("logged in!");
            getGamesJSON();
        })
        .fail(function(resp){
            console.log(resp);
            alert(`Something went wrong! Error code: ${ resp.status }, text: ${ resp.responseJSON.error }`);
        });
}


function logout() {
    $.post("/api/logout")
        .done(function() { console.log("logged out"); });
}

function signin(form) {
    $.post("/api/players",
        {name: form[0]["name"].value,
        pwd: form[0]["pwd"].value})
        .done(function () {
            console.log("new player created");
            getLeaderboardJSON();
        })
        .fail(function(resp){
            console.log(resp);
            alert('Sometging went wrong. Error code: ' + resp.status + ', message: ' + resp.responseJSON.error);
        });
}

function activateEventsListeners(){

    $('#logInButton').click(function(){
        let form = $("#login-form");
        login(form);
    });

    $('#signInButton').click(function(){
        let form = $("#login-form");
        signin(form);
    });
}

function isAnyUserLoggedIn(player) {
    return (player != null) ? true : false
}

function cleanLeaderboard() {
    $('#leaderboard').empty()
}

function printLeaderboard (lb){
	console.log(lb);

    $("#leaderboard").append(`<tr>
		                        <th class='bg'>Player ID</th>
		                        <th>Name</th>
		                        <th>Total Points</th>
		                        <th>Won</th>
		                        <th>Lost</th>
		                        <th>Tied</th>
		                      </tr>`);

    lb.sort((a, b) => b.results.sumOfPoints - a.results.sumOfPoints);
    lb.forEach((player) => {
        player.noOfmatches = player.results.won + player.results.lost + player.results.tied;
	});

    lb.forEach((player) => {
    	if (player.noOfmatches > 0){
            $("#leaderboard").append(`<tr>
				                        <td class='bg'>${ player.playerId }</td>
				                        <td>${ player.userName }</td>
				                        <td class='bg'>${ player.results.sumOfPoints }</td>
				                        <td>${ player.results.won }</td>
				                        <td>${ player.results.lost }</td>
				                        <td>${ player.results.tied }</td>
				                      </tr>`);
		}
	});
}

function cleanMainGameList(){
    $('#gameList').empty();
}

function printUserNameAndShowButtons(player) {
    $('#login-form').hide();
    $('#userName').append(`<div>User name: ${ player.email }</div>`);
    $('#newGame').show();
}

function printMainGameList (games) {
    // console.log(games);
    let loggedInUserId;

    (isAnyUserLoggedIn(games.player)) ? loggedInUserId = games.player.id : loggedInUserId = null;
    console.log('logged in user ID: ' + loggedInUserId);

    games.games.forEach((game) => {
        const creationDate = new Date(game.created);
        let playerTwo, scoresResult, scores;
        const options = { weekday: 'short', year: 'numeric', month: 'long',
            day: 'numeric', hour: 'numeric', minute: 'numeric', second: 'numeric' };
        const formattedDate = new Intl.DateTimeFormat('en-EN', options).format(creationDate);
        const gamePlayerForLoggidInUser = setGamePlayerIdForLoggedInUser(loggedInUserId, game);

        (game.gamePlayers.length < 2) ? playerTwo = "N/A" :
                                        playerTwo = game.gamePlayers[1].player.email;

        if (game.scores != undefined) {
            const scoreP0 = game.scores[0].score;
            const scoreP1 = game.scores[1].score;

            if (scoreP0 > scoreP1 && game.scores[0].playerId == game.gamePlayers[0].player.id) {
                scoresResult = game.gamePlayers[0].player.email;
            } else if (scoreP0 > scoreP1 && game.scores[0].playerId == game.gamePlayers[1].player.id) {
                scoresResult = game.gamePlayers[1].player.email;
            } else if (scoreP0 == scoreP1) {
                scoresResult = 'tie';
            } else if (scoreP0 < scoreP1 && game.scores[1].playerId == game.gamePlayers[0].player.id) {
                scoresResult = game.gamePlayers[0].player.email;
            } else {
                scoresResult = game.gamePlayers[1].player.email;
            }

            scores = (scoresResult == 'tie') ?
                'Game finished. Result: tie.' : 'Game finished. Player ' + scoresResult + ' won.';
        } else {
            scores = 'Game is not finished yet.';
        }

        // rendering game list with link to game, if logged in player is in this game
        if (game.gamePlayers[0].player.id === loggedInUserId ||
            (playerTwo !== "N/A" && game.gamePlayers[1].player.id === loggedInUserId)) {
            if(game.scores !== undefined){
                $("#gameList").append(`<li>
                                           <div>Game No: ${ game.id }, Created: ${ formattedDate }</div>
			                               <div>Player One: ${ game.gamePlayers[0].player.email },</div>
			                               <div>Player Two: ${ playerTwo },</div>
                                           <div>${ scores }</div>
                                       </li>`);
            } else {
                $("#gameList").append(`<li>
                                           <div><a href='/web/game.html?gp=${ gamePlayerForLoggidInUser }'>
                                           Game No: ${ game.id }, Created: ${ formattedDate },</a></div>
			                               <div>Player One: ${ game.gamePlayers[0].player.email },</div>
			                               <div>Player Two: ${ playerTwo },</div>
                                           <div>${ scores }</div>
                                       </li>`);
            }
        } else {
            $("#gameList").append(`<li>
                                       <div>Game No: ${ game.id }, Created: ${ formattedDate },</div>
			                           <div>Player One: ${ game.gamePlayers[0].player.email },</div>
			                           <div>Player Two: ${ playerTwo }</div>
                                       <div>${ scores }</div>
                                   </li>`);
        }

        if (showJoinButton(games.player, game, loggedInUserId)) {
            $('#gameList').append(`<button data-gameId=${ game.id } onclick=joinGame(${ game.id })>
                Join game ${ game.id } with ${ game.gamePlayers[0].player.email }</button>`);
        }
    });
}

function setGamePlayerIdForLoggedInUser(loggedInPlayerID, oneGame){

    if (loggedInPlayerID === oneGame.gamePlayers[0].player.id){
        return oneGame.gamePlayers[0].id;
    } else if (oneGame.gamePlayers.length > 1 && loggedInPlayerID === oneGame.gamePlayers[1].player.id){
        return oneGame.gamePlayers[1].id;
    } else {
        return null;
    }
}

function createNewGame() {
    $.post("/api/games")
        .done(function(resp) {
            // console.log("new game created!");
            console.log(resp);
            window.location = makeUrlForNewGameOrJoin(resp);

        })
        .fail(function(resp){
            console.log(resp);
            alert('Something went wrong!');
        });
}

function makeUrlForNewGameOrJoin(data){
    return '/web/game.html?gp=' + data.GamePlayerID;
}

function showJoinButton(player, game, loggedInUserId) {
    if(!isAnyUserLoggedIn(player) || game.gamePlayers.length > 1 || game.gamePlayers[0].player.id == loggedInUserId){
        return false;
    } else {
        return true;
    }
}

function joinGame(gameID) {
    $.post("/api/game/" + gameID + "/players")
        .done(function(resp) {
            console.log("you joined game " + gameID);
            console.log(resp);
            window.location = makeUrlForNewGameOrJoin(resp);

        })
        .fail(function(resp){
            console.log(resp);
            alert('Something went wrong!');
        });
}