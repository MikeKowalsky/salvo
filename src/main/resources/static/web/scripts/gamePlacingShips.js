
// $(document).ready(function(){
// });

function makeSavedShipsObject() {

    // console.log('in');

    let savedShipsObject = {"aircraftCarrier": [],
                            "battleship": [],
                            "submarine": [],
                            "destroyer": [],
                            "patrolBoat": []};

    let recivedDataArray = [];

    $(".savedShip").each(function () {
        // console.log($(this).data("info"));
        recivedDataArray.push($(this).data("info"));
    });
    // console.log(recivedDataArray);

    for(let key in savedShipsObject){
        recivedDataArray.forEach(gridLocation => {
            if(key == gridLocation.shipType){
                savedShipsObject[key].push(gridLocation.location);
            }
        });
    }
    // console.log(savedShipsObject);
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

    handleOnClick(buildGridIdsArray(vertical), vertical);
    handleOnMouseOver(buildGridIdsArray(vertical), vertical);

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

    $('#grid *').on("mouseover", function (e) {
        if (IDs.includes(e.target.id)) {
            $('#grid *').removeClass('placingShip');
            $('#grid *').removeClass('notAllowed');
            // console.log(e.target.id);
            showOrSaveShip(e.target.id, vertical, false);
        }
    });
}

function handleOnClick(IDs, vertical) {

    $('#grid *').on("click", function (e) {
        if (IDs.includes(e.target.id)) {
            console.log(e.target.id);
            showOrSaveShip(e.target.id, vertical, true);

            //deactivate 'this' shipRadioButton
            $("input[name='shipType']").each(function(){
                if (this.checked == true){
                    // console.log(this.id);
                    this.checked = false;
                    this.disabled = true;
                    $("label[for='" + this.id + "']").addClass('through');
                }
            });
        }

        //save, when all are located, ships position
        let placedShipsCounter = 0;
        $("input[name='shipType']").each(function(){
            if(this.disabled == true){
                placedShipsCounter++;
                if(placedShipsCounter == 5){
                    makeSavedShipsObject();
                }
            }
        });
        // if ($('#aircraftCarrier').disabled == true &&
        //     // $('#battleship').disabled == true &&
        //     // $('#submarine').disabled == true &&
        //     // $('#destroyer').disabled == true &&
        //     $('#patrolBoat').disabled == true){
        //     makeSavedShipsObject();
        // }
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

function showOrSaveShip(pointer, vertical, save) {

    let currentShip = whichShipIsOn();
    let currentOrient = whatOrientation();
    let pointerRow = pointer.charAt(0);
    let pointerCol = (pointer.charAt(2) == '0') ? '10' : pointer.charAt(1);
    // console.log("PointerRow: " + pointerRow + " / PointerCol: " + pointerCol + " / " + currentShip + " / " + currentOrient);


    let shipDO = {"aircraftCarrier": 5, "battleship": 4, "submarine": 3, "destroyer": 3, "patrolBoat": 2};

    switch (currentOrient) {
        case 'horizontal':
            for(let i = pointerCol; i < (parseInt(pointerCol) + shipDO[currentShip]); i++){
                if (isPositionAllowed(pointerCol, 'horizontal', currentShip)){
                    (save) ? addSaveClassAndCreateData(pointerRow + i, currentShip) : addPlacingShipClass(pointerRow + i);
                } else {
                    addNotAllowedClass(pointerRow + i);
                }
            }
            break;
        case 'portrait':
            let shipRows;
            shipRows = vertical.slice(vertical.indexOf(pointerRow), (vertical.indexOf(pointerRow) + shipDO[currentShip]));
            for(let i = 0; i < shipRows.length; i++){
                if (isPositionAllowed(pointerRow, 'portrait', currentShip)){
                    (save) ? addSaveClassAndCreateData(shipRows[i] + pointerCol, currentShip) : addPlacingShipClass(shipRows[i] + pointerCol);
                } else {
                    addNotAllowedClass(shipRows[i] + pointerCol);
                }
            }
            break;
    }


    // tutej
    /*
    let shipRows;
    switch (currentOrient){
        case 'horizontal':
            switch (currentShip){
                case 'aircraftCarrier':
                    for(let i = pointerCol; i < (parseInt(pointerCol) + 5); i++){
                        // (save) ? addSaveClassAndCreateData(pointerRow + i, 'aircraftCarrier') : addPlacingShipClass(pointerRow + i);
                        if (isPositionAllowed(pointerCol ,'horizontal', 'aircraftCarrier')){
                            (save) ? addSaveClassAndCreateData(pointerRow + i, 'aircraftCarrier') : addPlacingShipClass(pointerRow + i);
                        } else {
                            addNotAllowedClass(pointerRow + i);
                        }
                        // (isPositionAllowed(pointerCol ,'horizontal', 'aircraftCarrier')) ? addPlacingShipClass(pointerRow + i) : addNotAllowedClass(pointerRow + i);
                    }
                    break;
                case 'battleship':
                    for(let i = pointerCol; i < (parseInt(pointerCol) + 4); i++){
                        (save) ? addSaveClassAndCreateData(pointerRow + i, 'battleship') : addPlacingShipClass(pointerRow + i);
                    }
                    break;
                case 'submarine':
                    for(let i = pointerCol; i < (parseInt(pointerCol) + 3); i++){
                        (save) ? addSaveClassAndCreateData(pointerRow + i, 'submarine') : addPlacingShipClass(pointerRow + i);
                    }
                    break;
                case 'destroyer':
                    for(let i = pointerCol; i < (parseInt(pointerCol) + 3); i++){
                        (save) ? addSaveClassAndCreateData(pointerRow + i, 'destroyer') : addPlacingShipClass(pointerRow + i);
                    }
                    break;
                case 'patrolBoat':
                    for(let i = pointerCol; i < (parseInt(pointerCol) + 2); i++){
                        // $('#' + pointerRow + i).addClass('placingShip');
                        (save) ? addSaveClassAndCreateData(pointerRow + i, 'patrolBoat') : addPlacingShipClass(pointerRow + i);
                    }
                    break;
            }
            break;
        case 'portrait':
            switch (currentShip){
                case 'aircraftCarrier':
                    shipRows = vertical.slice(vertical.indexOf(pointerRow), (vertical.indexOf(pointerRow) + 5));
                    for(let i = 0; i < shipRows.length; i++){
                        (save) ? addSaveClassAndCreateData(shipRows[i] + pointerCol, 'aircraftCarrier') : addPlacingShipClass(shipRows[i] + pointerCol);
                    }
                    break;
                case 'battleship':
                    shipRows = vertical.slice(vertical.indexOf(pointerRow), (vertical.indexOf(pointerRow) + 4));
                    for(let i = 0; i < shipRows.length; i++){
                        (save) ? addSaveClassAndCreateData(shipRows[i] + pointerCol, 'battleship') : addPlacingShipClass(shipRows[i] + pointerCol);
                    }
                    break;
                case 'submarine':
                    shipRows = vertical.slice(vertical.indexOf(pointerRow), (vertical.indexOf(pointerRow) + 3));
                    for(let i = 0; i < shipRows.length; i++){
                        (save) ? addSaveClassAndCreateData(shipRows[i] + pointerCol, 'submarine') : addPlacingShipClass(shipRows[i] + pointerCol);
                    }
                    break;
                case 'destroyer':
                    shipRows = vertical.slice(vertical.indexOf(pointerRow), (vertical.indexOf(pointerRow) + 3));
                    for(let i = 0; i < shipRows.length; i++){
                        (save) ? addSaveClassAndCreateData(shipRows[i] + pointerCol, 'destroyer') : addPlacingShipClass(shipRows[i] + pointerCol);
                    }
                    break;
                case 'patrolBoat':
                    shipRows = vertical.slice(vertical.indexOf(pointerRow), (vertical.indexOf(pointerRow) + 2));
                    for(let i = 0; i < shipRows.length; i++){
                        // $('#' + shipRows[i] + pointerCol).addClass('placingShip');
                        // console.log(shipRows[i] + pointerCol);
                        (save) ? addSaveClassAndCreateData(shipRows[i] + pointerCol, 'patrolBoat') : addPlacingShipClass(shipRows[i] + pointerCol);
                    }
                    break;
            }
            break;
    }
    */
}

function addPlacingShipClass(id) {
    $('#' + id).addClass('placingShip');
}

function addNotAllowedClass(id) {
    $('#' + id).addClass('notAllowed');
}

function addPlacingShipShadowClass(id) {
    $('#' + id).addClass('placingShipShadow');
}

function addSaveClassAndCreateData(id, shipType) {
    $('#' + id).addClass('savedShip')
                .data("info", {"shipType": shipType,
                                "location": id});
}

function isPositionAllowed(relevantPointer, orientation, shipType) {

    let shipDO = {"aircraftCarrier": 5, "battleship": 4, "submarine": 3, "destroyer": 3, "patrolBoat": 2};

    switch (orientation) {
        case 'horizontal':
            return (parseInt(relevantPointer) + shipDO[shipType] <= 11);
        case 'portrait':
            let vertical = ['','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J']; // to nie powinno tu byc deklarowane!!!!!!!!!!!!
            let verticalAllowed = vertical.slice(1, (vertical.length - shipDO[shipType] + 1));
            return (verticalAllowed.includes(relevantPointer));
    }
}

// nie korzystam, bo jednak do save mam tą samą funkcje

// function savePosition(id, orientation, shipType){
//
//     let shipDO = {"aircraftCarrier": 5, "battleship": 4, "submarine": 3, "destroyer": 3, "patrolBoat": 2};
//
//     switch (orientation) {
//         case 'horizontal':
//             return (parseInt(col) + shipDO[shipType] <= 11) ? true : false;
//         case 'portrait':
//
//     }
// }