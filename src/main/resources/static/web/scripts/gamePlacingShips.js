
$(document).ready(function(){

    hideShipMarks();

});

function hideShipMarks() {
    $('#aircraftCarrierMark').hide();
    $('#battleshipMark').hide();
    $('#submarineMark').hide();
    $('#destroyerMark').hide();
    $('#patrolBoatMark').hide();
}

function whichShipIsOn() {

    let shipButtonsIDs  = ['aircraftCarrier', 'battleship', 'submarine', 'destroyer', 'patrolBoat'];
    for(let i=0; i<shipButtonsIDs.length; i++){
        if(document.getElementById(shipButtonsIDs[i]).checked){
            // console.log(shipButtonsIDs[i]);
            return shipButtonsIDs[i];
        }
    }

}

function isShipRadioButtonClicked() {

    let vertical = ['','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'];

    // let shipType = whichShipIsOn();
    // let elementID = '#' + shipType + 'Mark';
    // $(elementID).show();
    handleOnMouseOver(buildGridIdsArray(vertical), vertical);
    handleOnClick(buildGridIdsArray(vertical), vertical);

}

function buildGridIdsArray(vertical){

    let gridElementsArray = [];
    for(let i = 1; i < 11; i++){
        for(let j = 1; j < 11; j++){
            gridElementsArray.push(vertical[i] + j);
        }
    }
    return gridElementsArray;
}

function handleOnMouseOver(IDs, vertical) {
    // document.onmouseover = function(e) {
    //     if (IDs.includes(e.target.id)){
    //         // console.log(e.target.id);
    //         $('#grid *').removeClass('placingShip');
    //         showShip(e.target.id, vertical)
    //     }
    // }

    $('#grid *').mouseover(function (e) {
        if (IDs.includes(e.target.id)) {
            $('#grid *').removeClass('placingShip');
            // console.log(e.target.id);
            showShip(e.target.id, vertical, false);
        }
    });
}

function handleOnClick(IDs, vertical) {

    $('#grid *').click(function (e) {
        if (IDs.includes(e.target.id)) {
            console.log(e.target.id);
            showShip(e.target.id, vertical, true);
        }
    });
}

function whatOrientation() {
    if(document.getElementById('portrait').checked){
        // console.log("portrait");
        return 'portrait';
    } else {
        // console.log("horizontal");
        return 'horizontal';
    }
}

function showShip(pointer, vertical, save) {

    let currentShip = whichShipIsOn();
    let currentOrient = whatOrientation();
    let pointerRow = pointer.charAt(0);
    let pointerCol = (pointer.charAt(2) == '0') ? '10' : pointer.charAt(1);
    // console.log("PointerRow: " + pointerRow + " / PointerCol: " + pointerCol + " / " + currentShip + " / " + currentOrient);

    let shipRows;
    switch (currentOrient){
        case 'horizontal':
            switch (currentShip){
                case 'aircraftCarrier':
                    for(let i = pointerCol; i < (parseInt(pointerCol) + 5); i++){
                        // console.log('\'#' + pointerRow + i + '\'');
                        (save) ? addSaveShipClass(pointerRow + i) : addPlacingShipClass(pointerRow + i);
                    }
                    break;
                case 'battleship':
                    for(let i = pointerCol; i < (parseInt(pointerCol) + 4); i++){
                        (save) ? addSaveShipClass(pointerRow + i) : addPlacingShipClass(pointerRow + i);
                    }
                    break;
                case 'submarine':
                    for(let i = pointerCol; i < (parseInt(pointerCol) + 3); i++){
                        (save) ? addSaveShipClass(pointerRow + i) : addPlacingShipClass(pointerRow + i);
                    }
                    break;
                case 'destroyer':
                    for(let i = pointerCol; i < (parseInt(pointerCol) + 3); i++){
                        (save) ? addSaveShipClass(pointerRow + i) : addPlacingShipClass(pointerRow + i);
                    }
                    break;
                case 'patrolBoat':
                    for(let i = pointerCol; i < (parseInt(pointerCol) + 2); i++){
                        // $('#' + pointerRow + i).addClass('placingShip');
                        (save) ? addSaveShipClass(pointerRow + i) : addPlacingShipClass(pointerRow + i);
                    }
                    break;
            }
            break;
        case 'portrait':
            switch (currentShip){
                case 'aircraftCarrier':
                    shipRows = vertical.slice(vertical.indexOf(pointerRow), (vertical.indexOf(pointerRow) + 5));
                    for(let i = 0; i < shipRows.length; i++){
                        (save) ? addSaveShipClass(shipRows[i] + pointerCol) : addPlacingShipClass(shipRows[i] + pointerCol);
                    }
                    break;
                case 'battleship':
                    shipRows = vertical.slice(vertical.indexOf(pointerRow), (vertical.indexOf(pointerRow) + 4));
                    for(let i = 0; i < shipRows.length; i++){
                        (save) ? addSaveShipClass(shipRows[i] + pointerCol) : addPlacingShipClass(shipRows[i] + pointerCol);
                    }
                    break;
                case 'submarine':
                    shipRows = vertical.slice(vertical.indexOf(pointerRow), (vertical.indexOf(pointerRow) + 3));
                    for(let i = 0; i < shipRows.length; i++){
                        (save) ? addSaveShipClass(shipRows[i] + pointerCol) : addPlacingShipClass(shipRows[i] + pointerCol);
                    }
                    break;
                case 'destroyer':
                    shipRows = vertical.slice(vertical.indexOf(pointerRow), (vertical.indexOf(pointerRow) + 3));
                    for(let i = 0; i < shipRows.length; i++){
                        (save) ? addSaveShipClass(shipRows[i] + pointerCol) : addPlacingShipClass(shipRows[i] + pointerCol);
                    }
                    break;
                case 'patrolBoat':
                    shipRows = vertical.slice(vertical.indexOf(pointerRow), (vertical.indexOf(pointerRow) + 2));
                    for(let i = 0; i < shipRows.length; i++){
                        // $('#' + shipRows[i] + pointerCol).addClass('placingShip');
                        // console.log(shipRows[i] + pointerCol);
                        (save) ? addSaveShipClass(shipRows[i] + pointerCol) : addPlacingShipClass(shipRows[i] + pointerCol);
                    }
                    break;
            }
            break;
    }

}

function addPlacingShipClass(id) {
    $('#' + id).addClass('placingShip');
}

function addPlacingShipShadowClass(id) {
    $('#' + id).addClass('placingShipShadow');
}

function addSaveShipClass(id) {
    $('#' + id).addClass('saveShip');
}

/* Drag and drop handling */

function drag(ev) {
    ev.dataTransfer.setData("text", ev.target.id);
}

function drop(ev) {
    ev.preventDefault();
    let data = ev.dataTransfer.getData("text");
    ev.target.appendChild(document.getElementById(data));
}

function allowDrop(ev) {
    ev.preventDefault();
}







