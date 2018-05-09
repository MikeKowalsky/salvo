
$(document).ready(function(){

    let gamePlayerId = GetQueryString();
    console.log(gamePlayerId);

    $.ajax({url: makeUrl(gamePlayerId.gp), success: function(result){
            console.log(result);
            header(result, gamePlayerId.gp);
            printGrid();
            markShips(result);
    }});

});

window.GetQueryString = function(q) {
    let vars = [], hash;
    let hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(let i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        vars[hash[0]] = hash[1];
    }
    return vars;
};

function makeUrl(playerID) {
    return 'http://localhost:8080/api/game_view/' + playerID;
}

function header(dataFromAjaxCall, gamePlayerId) {

    $('#gameNo').append('<h2>Game id: '+ dataFromAjaxCall.gameId + ' , Created:  '
        + new Date(dataFromAjaxCall.created) + '</h2>');
    $('#gameNo').append('<p id="p1">Player One: ' + dataFromAjaxCall.gamePlayers[0].player.email + '</p>');
    $('#gameNo').append('<p id="p2">Player Two: ' + dataFromAjaxCall.gamePlayers[1].player.email + '</p>');


    if (gamePlayerId == dataFromAjaxCall.gamePlayers[0].id){
        $('#p1').append('<span>(you)</span>').addClass('bold');
    } else {
        $('#p2').append('<span>(you)</span>').addClass('bold');
    }

}

function printGrid() {

    let vertical = ['','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'];

    for (let r = 0; r < 11; r++) {
        let column = "";
        let horizontalPosition = 0;

        for (let c = 0; c < 11; c++) {
            if (r == 0 & c > 0){
                column += "<td id='" + vertical[r] + horizontalPosition + "'>" + c + "</td>";
            } else if (r > 0 & c == 0) {
                column += "<td id='" + vertical[r] + horizontalPosition + "'>" + vertical[r] + "</td>";
            } else {
                column += "<td id='" + vertical[r] + horizontalPosition + "'></td>";
            }
            horizontalPosition++;
        }
        $("#grid").append("<tr id='" + vertical[r] + "'>" + column + "</tr>");
    }
}

function markShips(dataFromAjaxCall) {
    let playersShipsLocations = [];
    dataFromAjaxCall.ships.forEach((ship) => {
        ship.locations.forEach((location) => playersShipsLocations.push(location));
    });
    console.log(playersShipsLocations);

    playersShipsLocations.forEach((location) => $('#' + location).addClass('playersShip'));
}


