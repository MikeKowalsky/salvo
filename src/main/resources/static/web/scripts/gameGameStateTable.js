
function createGameStateTable(data) {

    let hASArray = data.hAS;
    hASArray.sort((turn1, turn2) => turn1.turnNo - turn2.turnNo);

    hASArray.forEach((turn) => {
        let addTurnNoCell = 0
        for (key in turn.hitsOnPlayer){

            let tdId = turn.turnNo + "_" + key.charAt(0);
            let currentKey = key;

            $('#gameState table tbody').append("<tr id='" + tdId + "'></tr>");

            if (addTurnNoCell === 0){
                $('#' + tdId).append("<td rowspan='" + Object.keys(turn.hitsOnPlayer).length + "'>" + turn.turnNo + "</td>");
            }
            addTurnNoCell++;

            addRowsForGivenPlayer(turn.hitsOnPlayer, currentKey,tdId);
            addRowsForGivenPlayer(turn.hitsOnEnemy, currentKey, tdId);
        }
    });
}

function addRowsForGivenPlayer(hitsObject, passedKey, id) {

    for(key in hitsObject){
        if(key == passedKey){

            if(hitsObject[key].isSink){
                $('#' + id).append("<td class='sinked'>" + key + "</td>");
            } else {
                $('#' + id).append("<td>" + key + "</td>");
            }

            if(hitsObject[key].hits.length > 0){
                $('#' + id).append("<td class='hit'>" + hitsObject[key].hits.length + " / " + hitsObject[key].size + "</td>");
            } else {
                $('#' + id).append("<td>" + hitsObject[key].hits.length + " / " + hitsObject[key].size + "</td>");
            }
        }
    }
}
