
function createGameStateTable(data) {

    let hASArray = data.hAS;
    hASArray.sort((turn1, turn2) => turn1.turnNo - turn2.turnNo);

    hASArray.forEach((turn) => {
        for (key in turn.hitsOnPlayer){

            let tdId = turn.turnNo + "_" + key.charAt(0);
            let currentKey = key;

            $('#gameState table tbody').append("\
                <tr id='" + tdId + "'>\
                    <td>" + turn.turnNo + "</td>\
                </tr>\
            ");

            addRowsForGivenPlayer(turn.hitsOnPlayer, currentKey,tdId);
            addRowsForGivenPlayer(turn.hitsOnEnemy, currentKey, tdId);
        }
    });
}

function addRowsForGivenPlayer(hitsObject, passedKey, id) {

    for(key in hitsObject){
        if(key == passedKey){
            $('#' + id).append("\
                <td>" + key + "</td>\
                <td>" + hitsObject[key].hits.length + " / " + hitsObject[key].size + "</td>\
                ");
        }
    }
}