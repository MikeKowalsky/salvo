
$(document).ready(function(){

    let gamePlayerId = GetQueryString();
    console.log(gamePlayerId);

    $.ajax({url: makeUrl(gamePlayerId.gp), success: function(result){
            console.log(result);
            printGamePage(result, gamePlayerId.gp);
    }});

});

function logout() {
    $.post("/api/logout")
        .done(function() { window.location = '/web/games.html' });
}

function printGamePage(data, gpId){

    let playerId = getPlayerId(data,gpId);
    let enemyId = getEnemyId(data,gpId);

    header(data, gpId);
    printGrid('#grid');
    printGrid('#salvoGrid');
    markGrids(data, gpId);
    let shipsLocationArray = markShips(data);
    markSalvos(data, playerId, 'owner', shipsLocationArray);
    markSalvos(data, enemyId, 'enemy', shipsLocationArray);

}

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

function getPlayerId(data, gpId) {
    return (gpId == data.gamePlayers[0].id) ? data.gamePlayers[0].player.id : data.gamePlayers[1].player.id;
}

function getEnemyId(data, gpId) {
    return (gpId == data.gamePlayers[0].id) ? data.gamePlayers[1].player.id : data.gamePlayers[0].player.id;
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

function printGrid(elementID) {

    let vertical = ['','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'];

    for (let r = 0; r < 11; r++) {
        let column = "";
        let horizontalPosition = 0;

        for (let c = 0; c < 11; c++) {
            if (r == 0 & c > 0){
                column += "<td class='gridNames'>" + c + "</td>";
            } else if (r > 0 & c == 0) {
                column += "<td class='gridNames'>" + vertical[r] + "</td>";
            } else if (elementID == '#grid'){
                column += "<td id='" + vertical[r] + horizontalPosition + "'></td>";
            } else if (elementID == '#salvoGrid'){
                column += "<td id='s" + vertical[r] + horizontalPosition + "'></td>";
            }
            horizontalPosition++;
        }
        $(elementID).append("<tr id='" + vertical[r] + "'>" + column + "</tr>");
    }
}

function markGrids(dataFromAjaxCall, gamePlayerId) {
    if (gamePlayerId == dataFromAjaxCall.gamePlayers[0].id){
        $('#gridOne').append('<p>' + dataFromAjaxCall.gamePlayers[0].player.email + '(you)</p>').addClass('bold');
        $('#gridTwo').append('<p>' + dataFromAjaxCall.gamePlayers[1].player.email + '</p>');
    } else {
        $('#gridOne').append('<p>' + dataFromAjaxCall.gamePlayers[1].player.email + '(you)</p>').addClass('bold');
        $('#gridTwo').append('<p>' + dataFromAjaxCall.gamePlayers[0].player.email + '</p>');
    }
}

function markShips(data) {

    let shipsLocations = [];
    data.ships.forEach((ship) => {
        ship.locations.forEach((location) => shipsLocations.push(location));
    });
    console.log('shipsLocations: ' + shipsLocations);

    shipsLocations.forEach((location) => $('#' + location).addClass('playersShip'));

    return shipsLocations;
}

function markSalvos(data, pID, playerType, shipsLocations) {

    let salvoLocations = {};
    data.salvoes.forEach((salvo) => salvoLocations[salvo.turnNo] = []);
    data.salvoes.forEach((salvo) => {
        if (pID == salvo.playerId){
            salvo.locations.forEach((location) => salvoLocations[salvo.turnNo].push(location));
        }
    });
    console.log(playerType);
    console.log(salvoLocations);

    for (let key in salvoLocations){
        salvoLocations[key].forEach((location) => {
            if (playerType === 'owner'){
                $('#s' + location).addClass('playersSalvo').append(key);
            } else {
                if (shipsLocations.includes(location)){
                    console.log('hit: ' + location);
                    $('#' + location).addClass('enemysSalvoHit').append(key);
                } else {
                    $('#' + location).addClass('enemysSalvo').append(key);
                }
            }
        });
    }
}


