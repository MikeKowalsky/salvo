
function header(data, gamePlayerId) {

    $('#tableGameID').append(`Game No: ${ data.gameId }`);
    let date = new Date(data.created);
    $('#tableCreated').append(`Created: ${ date }`);

    let playerMail, enemyMail;

    if(enemyExist(data)){
        if(data.gamePlayers[0].id === gamePlayerId){
            playerMail = data.gamePlayers[0].player.email;
            enemyMail = data.gamePlayers[1].player.email;
        } else {
            playerMail = data.gamePlayers[1].player.email;
            enemyMail = data.gamePlayers[0].player.email;
        }

        $('#tablePlayers').append(`<td>Playing: </td>
                                   <td colspan="6">${ playerMail } vs ${ enemyMail }</td>`);
    } else {
        $('#tablePlayers').append(`<td>Playing: </td>
                    <td colspan="6">${ data.gamePlayers[0].player.email } vs N/A (waiting for another player ...)</td>`);
    }

    lastTurnRow(data, enemyMail);
    addGameStatus(data);
}

function lastTurnRow(data, enemyMail) {

    let hASArray = data.hAS;
    if (hASArray != null){
        hASArray.sort((turn1, turn2) => turn2.turnNo - turn1.turnNo);

        let [lastTurn] = hASArray;

        $('#tableLastTurnRow').append(`<td rowspan="2">Last turn no</td>
                                       <td  colspan="3">Hits on you</td>
                                       <td  colspan="3">Hits on ${ enemyMail }</td>`);

        $('#tableLastTurnSecondRow').append(`<td>Ship type</td>
                                             <td>Hits till now</td>
                                             <td>Hits in this turn</td>
                                             <td>Ship type</td>
                                             <td>Hits till now</td>
                                             <td>Hits in this turn</td>`);

        $('#row-aircraftCarrier').append(`<td rowspan='5'>${ lastTurn.turnNo }</td>`)

        addRowsForGivenPlayer(lastTurn.hitsOnPlayer);
        addRowsForGivenPlayer(lastTurn.hitsOnEnemy);
    }
}

function addRowsForGivenPlayer(hitsObject) {

    for(shipType in hitsObject){
        if(hitsObject[shipType].isSink){
            $(`#row-${ shipType }`).append("<td class='sinked'>" + shipType + "</td>");
            $(`#row-${ shipType }`).append(
                `<td class='hit'> ${ hitsObject[shipType].hitsTillNow } / ${ hitsObject[shipType].size }</td>`);
        } else {
            $(`#row-${ shipType }`).append("<td>" + shipType + "</td>");
            $(`#row-${ shipType }`).append(
                `<td> ${ hitsObject[shipType].hitsTillNow } / ${ hitsObject[shipType].size }</td>`);
        }

        if(hitsObject[shipType].hits.length > 0){
            $(`#row-${ shipType }`).append("<td class='hit'>" + hitsObject[shipType].hits.length + "</td>");
        } else {
            $(`#row-${ shipType }`).append("<td>" + hitsObject[shipType].hits.length + "</td>");
        }
    }

}

function addGameStatus(data) {

    let  msg = "Something weird is happening!";

    if(data.gameStatus.isGameOver){
        if(data.gameStatus.whoWon === -1){
            msg = "Tie! Both players finished the game in the same turn.";
        } else {
            let winnerPlayerId = data.gameStatus.whoWon;
            let winnerEmail;
            if (data.gamePlayers[0].player.id === winnerPlayerId){
                winnerEmail = data.gamePlayers[0].player.email;
            } else {
                winnerEmail = data.gamePlayers[1].player.email;
            }

            if(data.loggedInName === winnerEmail){
                msg = `Game over! Congratulations, you won!`
            } else {
                msg = `Game over! You lost the game, player ${ winnerEmail } won.`;
            }
        }
    } else {
        if(data.gameStatus.status == null) {
            msg = 'Waiting for second player.';
        } else if (data.gameStatus.status === "WaitingForSecondPlayer") {
            msg = 'Please wait for second player.'
        } else if (data.gameStatus.status === "WaitingForShips"){
            msg = 'Please place your ships on the grid and save them!';
        } else if (data.gameStatus.status === 'WaitingForEnemy'){
            msg = "Waiting for enemy's salvo.";
        } else if (data.gameStatus.status === "WaitingForSalvoes"){
            msg = "Please place your salvo.";
        }
    }


    $('#gameStatusRow').append(`<td colspan="2">Game status: </td>
                                <td colspan="5">${ msg }</td>`);
}
