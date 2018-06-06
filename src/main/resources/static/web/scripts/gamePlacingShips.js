
// $(document).ready(function(){
// });

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
            $('#grid *').removeClass('placingShipShadow');
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

            // break if clicked - Save - but ship is in not allowed position
            if ($('.notAllowed').length > 0) {
                return null;
            }

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

        //save, when all are located, ships position in Data Object
        let placedShipsCounter = 0;
        $("input[name='shipType']").each(function(){
            if(this.disabled == true){
                placedShipsCounter++;
                if(placedShipsCounter == 5){
                    makeSavedShipsObject();
                }
            }
        });
    });
}

function makeSavedShipsObject() {

    // console.log('in');

    let savedShipsObject = {"aircraftCarrier": [],
        "battleship": [],
        "submarine": [],
        "destroyer": [],
        "patrolBoat": []};

    let receivedDataArray = makeReceivedDataArray();

    for(let key in savedShipsObject){
        receivedDataArray.forEach(gridLocation => {
            if(key == gridLocation.shipType){
                savedShipsObject[key].push(gridLocation.location);
            }
        });
    }
    // console.log(savedShipsObject);
}

function makeReceivedDataArray(){
    let receivedDataArray = [];

    $(".savedShip").each(function () {
        // console.log($(this).data("info"));
        receivedDataArray.push($(this).data("info"));
    });
    // console.log(receivedDataArray);
    return receivedDataArray;
}

function makeAlreadyTakenIDs(receivedDataArray){

    let alreadyTakenIDs = [];
    receivedDataArray.forEach(data => {
        for(let key in data){
            alreadyTakenIDs.push(data[key]);
        }
    });
    return alreadyTakenIDs;
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

    // let alreadyTakenIDs = makeAlreadyTakenIDs(makeReceivedDataArray());
    // if (alreadyTakenIDs === undefined || ){
    // }

    // let currentShip = [];
    let currentShipType = whichShipIsOn();
    let currentOrient = whatOrientation();
    let pointerRow = pointer.charAt(0);
    let pointerCol = makeColFromID(pointer);
    // console.log("PointerRow: " + pointerRow + " / PointerCol: " + pointerCol + " / " + currentShipType + " / " + currentOrient);

    let shipDO = {"aircraftCarrier": 5, "battleship": 4, "submarine": 3, "destroyer": 3, "patrolBoat": 2};

    // Ship
    switch (currentOrient) {
        case 'horizontal':
            for(let i = pointerCol; i < (pointerCol + shipDO[currentShipType]); i++){
                // currentShip.push(pointerRow + i);
                if(!isPositionAllowed(pointerCol, 'horizontal', currentShipType, vertical, shipDO)){
                    addNotAllowedClass(pointerRow + i);
                } else {
                    (save) ? addSaveClassAndCreateData(pointerRow + i, currentShipType) : addPlacingShipClass(pointerRow + i);
                }
            }
            // currentShip.forEach(id => {
            //     if(!isPositionAllowed(id, 'horizontal', currentShipType, vertical, shipDO)){
            //         addNotAllowedClass(id);
            //     } else {
            //         (save) ? addSaveClassAndCreateData(id, currentShipType) : addPlacingShipClass(id);
            //     }
            // });

            break;
        case 'portrait':
            let shipRows = vertical.slice(vertical.indexOf(pointerRow), (vertical.indexOf(pointerRow) + shipDO[currentShipType]));
            for(let i = 0; i < shipRows.length; i++){
                if (!isPositionAllowed(pointerRow, 'portrait', currentShipType, vertical, shipDO)){
                    addNotAllowedClass(shipRows[i] + pointerCol);
                } else {
                    (save) ? addSaveClassAndCreateData(shipRows[i] + pointerCol, currentShipType) : addPlacingShipClass(shipRows[i] + pointerCol);
                }
            }
            break;
    }

    // Shadow
    switch (currentOrient) {
        case 'horizontal':
            let shipRowsHorizontal = vertical.slice(vertical.indexOf(pointerRow) - 1, (vertical.indexOf(pointerRow) + 2));
            for(let i = (pointerCol - 1); i < (pointerCol + shipDO[currentShipType] + 1); i++){
                for(let j = 0; j < shipRowsHorizontal.length; j++){
                    addPlacingShipShadowClass(shipRowsHorizontal[j] + i);
                }
            }
            break;
        case 'portrait':
            let shipRows = vertical.slice(vertical.indexOf(pointerRow) - 1, (vertical.indexOf(pointerRow) + shipDO[currentShipType] + 1));
            for(let i = 0; i < shipRows.length; i++){
                for(let j = (pointerCol - 1); j < (pointerCol + 2); j++){
                    addPlacingShipShadowClass((shipRows[i] + j));
                }
            }
            break;
    }
}

function makeColFromID(id){
    return parseInt((id.charAt(2) === '0') ? '10' : id.charAt(1));
}

function addPlacingShipClass(id) {
    $('#' + id).addClass('placingShip');
}

function addNotAllowedClass(id) {
    $('#' + id).addClass('notAllowed');
}

function addPlacingShipShadowClass(id) {
    let $elementWithID =  $('#' + id);
    if ($elementWithID.hasClass('placingShip') ||
        $elementWithID.hasClass('notAllowed') ||
        $elementWithID.hasClass('savedShip')){
        return null;
    } else {
        $elementWithID.addClass('placingShipShadow');
    }
}

function addSaveClassAndCreateData(id, shipType) {
    $('#' + id).addClass('savedShip')
                .data("info", {"shipType": shipType,
                                "location": id});
}

// function isPositionAllowed(id, orientation, shipType, vertical, shipDO) {
//     switch (orientation) {
//         case 'horizontal':
//             return (makeColFromID(id) + shipDO[shipType] <= 11);
//         case 'portrait':
//             let verticalAllowed = vertical.slice(1, (vertical.length - shipDO[shipType] + 1));
//             return (verticalAllowed.includes(id.charAt(0)));
//     }
// }

function isPositionAllowed(relevantPointer, orientation, shipType, vertical, shipDO) {
    switch (orientation) {
        case 'horizontal':
            return (relevantPointer + shipDO[shipType] <= 11);
        case 'portrait':
            let verticalAllowed = vertical.slice(1, (vertical.length - shipDO[shipType] + 1));
            return (verticalAllowed.includes(relevantPointer));
    }
}
