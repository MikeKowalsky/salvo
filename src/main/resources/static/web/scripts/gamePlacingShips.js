
function isShipRadioButtonClicked() {
    let vertical = ['','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'];
    handleOnMouseOver(buildGridIdsArray(vertical, 'forShips'), vertical);
}

function whichShipIsOn() {

    let shipButtonsIDs  = ['aircraftCarrier', 'battleship', 'submarine', 'destroyer', 'patrolBoat'];
    for(let i=0; i<shipButtonsIDs.length; i++){
        if(document.getElementById(shipButtonsIDs[i]).checked){
            return shipButtonsIDs[i];
        }
    }
}

function whatOrientation() {
    if(document.getElementById('portrait').checked){
        return 'portrait';
    } else {
        return 'horizontal';
    }
}

function buildGridIdsArray(vertical, gridType){
    let gridElementsArray = [];
    for(let i = 1; i < 11; i++){
        for(let j = 1; j < 11; j++){
            (gridType === 'forShips') ?
                gridElementsArray.push(vertical[i] + j) : gridElementsArray.push('s' + vertical[i] + j) ;
        }
    }
    return gridElementsArray;
}

function handleOnMouseOver(IDs, vertical) {
    let currentData = [];

    $('#grid').on("mouseover", function (e) {

        $('#grid').off("click"); //sprawdzic czy mozna nie korzystac z tego jesli jest located ship
        e.stopPropagation();
        removePastClasses();

        if (IDs.includes(e.target.id)) {
            // console.log(e.target.id);
            currentData = showOrSaveShip(e.target.id, IDs, vertical, false);
            // console.log(currentData);
            handleOnClick(IDs, vertical, currentData);
        }
    });
}

function handleOnClick(IDs, vertical, currentData) {

    $('#grid').on("click", function (e) {

        e.stopPropagation();

        if (IDs.includes(e.target.id)) {

            // break if clicked - Save - but ship is in not allowed position
            if ($('.notAllowed').length > 0 || $('.notAllowedShadow').length > 0) {
                return null;
            }

            console.log(e.target.id);
            console.log(currentData);

            addSaveClassAndCreateData(currentData[0], currentData[1]);

            //deactivate 'this' shipRadioButton
            $("input[name='shipType']").each(function(){
                if (this.checked === true){
                    // console.log(this.id);
                    this.checked = false;
                    this.disabled = true;
                    $("label[for='" + this.id + "']").addClass('through');
                }
            });

            //save, when all are located, ships position in Data Object
            let placedShipsCounter = 0;
            let placedShips = null;

            $("input[name='shipType']").each(function(){
                if(this.disabled === true){
                    placedShipsCounter++;
                    placedShips = makeSavedShipsObject();
                }
            });

            // activate event for dragging ship if any is located
            if(placedShipsCounter > 0){
                handleDraggingEventListener(placedShips);
            }

            // 5 shipsLocated so show DO
            if(placedShipsCounter === 5){
                console.log(placedShips);
                $('#savePositions').data('dataToSend', placedShips);
                $('#savePositionsDiv').show();
            }
        }
    });
}

function handleDraggingEventListener(placedShips) {

    $('#grid').on("click", function (e) {

        e.stopPropagation();
        let $idToDrag = $('#' + e.target.id);

        if ($idToDrag.hasClass('savedShip')){
            placedShips.forEach((placedShip) => {
                if(placedShip.shipType === $idToDrag.data("info").shipType){
                    console.log("chosen-to-drag Ship location: " + placedShip.shipType + " / " + placedShip.locations);

                    // remove savedShipClass
                    placedShip.locations.forEach(id => {
                        $('#' + id).removeClass('savedShip').empty();
                    });

                    //activate radio button again
                    console.log(placedShip.shipType);
                    $('#' + placedShip.shipType).removeAttr('disabled').prop("checked", true);
                    $("label[for='" + placedShip.shipType + "']").removeClass('through');
                }
            });
        }
    });
}

function makeSavedShipsObject() {
    let savedShipsArray = [{shipType: "aircraftCarrier", locations: []},
        {shipType: "battleship", locations: []},
        {shipType: "submarine", locations: []},
        {shipType: "destroyer", locations: []},
        {shipType: "patrolBoat", locations: []}];


    // [ { type: "destroyer", locations: ["E1", "F1", "G1"] },
    // { type: "patrol boat", locations: ["I5", "I6"] }])

    let receivedDataArray = makeReceivedDataArray();

    savedShipsArray.forEach((shipType) => {
        receivedDataArray.forEach(gridLocation => {
            if(shipType.shipType === gridLocation.shipType){
                shipType.locations.push(gridLocation.locations);
            }
        });
    });
    // console.log(savedShipsArray);
    return savedShipsArray;
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

function showOrSaveShip(pointer, gridIDs, vertical) {

    let currentShipType = whichShipIsOn();
    let currentOrient = whatOrientation();
    let pointerRow = pointer.charAt(0);
    let pointerCol = makeColFromID(pointer);
    // console.log("PointerRow: " + pointerRow + " / PointerCol: " + pointerCol + " / " + currentShipType + " / " + currentOrient);

    let shipDO = {"aircraftCarrier": 5, "battleship": 4, "submarine": 3, "destroyer": 3, "patrolBoat": 2};

    // Ship
    let currentShip = [];
    if (currentOrient === 'horizontal') {
        for(let i = pointerCol; i < (pointerCol + shipDO[currentShipType]); i++) {
            if (i > 0 && i <11) {
                currentShip.push(pointerRow + i);
            }
        }
    } else {
        let shipRows = vertical.slice(vertical.indexOf(pointerRow), (vertical.indexOf(pointerRow) + shipDO[currentShipType]));
        for(let i = 0; i < shipRows.length; i++) {
            currentShip.push(shipRows[i] + pointerCol);
        }
    }

    currentShip.forEach(id => {
        ((!gridIDs.includes(id)) || currentShip.length !== shipDO[currentShipType]) ?
            addNotAllowedClass(currentShip) : addPlacingShipClass(currentShip);
    });

    // Shadow
    let currentShadow = [];
    if (currentOrient === 'horizontal'){
        let shipRowsHorizontal = vertical.slice(vertical.indexOf(pointerRow) - 1, (vertical.indexOf(pointerRow) + 2));
        for(let i = (pointerCol - 1); i < (pointerCol + shipDO[currentShipType] + 1); i++){
            for(let j = 0; j < shipRowsHorizontal.length; j++){
                currentShadow.push(shipRowsHorizontal[j] + i);
            }
        }
    } else {
        let shipRows = vertical.slice(vertical.indexOf(pointerRow) - 1, (vertical.indexOf(pointerRow) + shipDO[currentShipType] + 1));
        for(let i = 0; i < shipRows.length; i++){
            for(let j = (pointerCol - 1); j < (pointerCol + 2); j++){
                currentShadow.push(shipRows[i] + j);
            }
        }
    }

    currentShadow.forEach(id => {
        if (gridIDs.includes(id)) {
            addPlacingShipShadowClass(id);
        }
    });

    if(isSavedAndShadow(currentShadow)){
        addNotAllowedClass(currentShip);
        addNotAllowedShadowClass(currentShadow);
    }

    let currentData = [currentShip, currentShipType];
    // console.log(currentData);
    return currentData;
}

function makeColFromID(id){
    return parseInt((id.charAt(2) === '0') ? '10' : id.charAt(1));
}

function addPlacingShipClass(currentShip) {

    currentShip.forEach(id => {
        let $elementWithID =  $('#' + id);
        if ($elementWithID.hasClass('savedShip')){
            return null;
        } else {
            $elementWithID.addClass('placingShip')
        }
    });
}

function addNotAllowedClass(currentShip) {

    currentShip.forEach(id => {
        let $elementWithID =  $('#' + id);
        if ($elementWithID.hasClass('savedShip')){
            return null;
        } else {
            $elementWithID.addClass('notAllowed')
        }
    });
}

function addPlacingShipShadowClass(id) {
    let $elementWithID =  $('#' + id);
    if ($elementWithID.hasClass('placingShip') ||
        $elementWithID.hasClass('notAllowed')){
        return null;
    } else {
        $elementWithID.addClass('placingShipShadow');
    }
}

function addNotAllowedShadowClass(currentShadow){

    currentShadow.forEach(id => {
        let $elementWithID =  $('#' + id);
        if ($elementWithID.hasClass('placingShip') ||
            $elementWithID.hasClass('notAllowed')){
            return null;
        } else {
            $elementWithID.addClass('notAllowedShadow');
        }
    });
}

function addSaveClassAndCreateData(currentShip, shipType) {

    currentShip.forEach(id => {
        let $elementWithID =  $('#' + id);
        if($elementWithID.hasClass('notAllowed')){
            return null;
        }

        $elementWithID
        $elementWithID.addClass('savedShip')
                        .data("info", {"shipType": shipType,
                                        "locations": id})
                        .append(giveShipTypeShortcut(shipType));
    });
}

function removePastClasses(){
    let $allGridElements = $('#grid *');
    $allGridElements.removeClass('placingShip');
    $allGridElements.removeClass('notAllowed');
    $allGridElements.removeClass('placingShipShadow');
    $allGridElements.removeClass('notAllowedShadow');
}

function isSavedAndShadow(currentShadow) {
    let counter = 0;
    let alreadyTakenIDs = makeAlreadyTakenIDs();

    alreadyTakenIDs.forEach(id => {
        if(currentShadow.includes(id)){
            counter++;
        }
    });
    return (counter !== 0);
}

function makeAlreadyTakenIDs(){
    let alreadyTakenIDs = [];

    $('.savedShip').each(function () {
        alreadyTakenIDs.push(this.id);
    });
    return alreadyTakenIDs;
}

function giveShipTypeShortcut(shipType) {

    switch (shipType) {
        case 'aircraftCarrier' : return 'AC';
        case 'battleship' : return 'BS';
        case 'submarine' : return 'SM';
        case 'destroyer' : return 'DE';
        case 'patrolBoat' : return 'PB';
    }

}

