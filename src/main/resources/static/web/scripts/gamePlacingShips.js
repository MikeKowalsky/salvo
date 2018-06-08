
function isShipRadioButtonClicked() {
    let vertical = ['','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'];
    handleOnMouseOver(buildGridIdsArray(vertical), vertical);
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
    let currentData = [];

    $('#grid').on("mouseover", function (e) {

        $('#grid').off("click");
        e.stopPropagation();

        if (IDs.includes(e.target.id)) {
            $('#grid *').removeClass('placingShip');
            $('#grid *').removeClass('notAllowed');
            $('#grid *').removeClass('placingShipShadow');
            $('#grid *').removeClass('notAllowedShadow');
            // console.log(e.target.id);
            currentData = showOrSaveShip(e.target.id, IDs, vertical, false);
            // console.log(currentData);
            handleOnClick(buildGridIdsArray(vertical), vertical, currentData);
        }
    });
}

function handleOnClick(IDs, vertical, currentData) {

    $('#grid').on("click", function (e) {

        e.stopPropagation();

        if (IDs.includes(e.target.id)) {
            // console.log(e.target.id);
            console.log(currentData);
            addSaveClassAndCreateData(currentData[0], currentData[1]);

            // break if clicked - Save - but ship is in not allowed position
            if ($('.notAllowed').length > 0 || $('.notAllowedShadow').length > 0) {
                return null;
            }

            //deactivate 'this' shipRadioButton
            $("input[name='shipType']").each(function(){
                if (this.checked === true){
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
            if(this.disabled === true){
                placedShipsCounter++;
                if(placedShipsCounter === 5){
                    makeSavedShipsObject();
                }
            }
        });
    });
}

function makeSavedShipsObject() {
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
    console.log(savedShipsObject);
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

function showOrSaveShip(pointer, gridIDs, vertical, save) {

    let currentShip = [];
    let currentShadow = [];
    let currentShipType = whichShipIsOn();
    let currentOrient = whatOrientation();
    let pointerRow = pointer.charAt(0);
    let pointerCol = makeColFromID(pointer);
    // console.log("PointerRow: " + pointerRow + " / PointerCol: " + pointerCol + " / " + currentShipType + " / " + currentOrient);

    let shipDO = {"aircraftCarrier": 5, "battleship": 4, "submarine": 3, "destroyer": 3, "patrolBoat": 2};

    // Ship
    switch (currentOrient) {
        case 'horizontal':
            for(let i = pointerCol; i < (pointerCol + shipDO[currentShipType]); i++) {
                if (i > 0 && i <11) {
                    currentShip.push(pointerRow + i);
                }
            }
            // console.log(currentShip);
            currentShip.forEach(id => {
                if((!gridIDs.includes(id)) || currentShip.length !== shipDO[currentShipType]){
                    addNotAllowedClass(currentShip);
                } else {
                    (save) ? addSaveClassAndCreateData(currentShip, currentShipType) : addPlacingShipClass(currentShip);
                }
            });
            break;
        case 'portrait':
            let shipRows = vertical.slice(vertical.indexOf(pointerRow), (vertical.indexOf(pointerRow) + shipDO[currentShipType]));
            for(let i = 0; i < shipRows.length; i++) {
                currentShip.push(shipRows[i] + pointerCol);
            }
            // console.log(currentShip);
            currentShip.forEach(id => {
                if ((!gridIDs.includes(id)) || currentShip.length !== shipDO[currentShipType]){
                    addNotAllowedClass(currentShip);
                } else {
                    (save) ? addSaveClassAndCreateData(currentShip, currentShipType) : addPlacingShipClass(currentShip);
                }
            });
            break;
    }

    // Shadow
    switch (currentOrient) {
        case 'horizontal':
            let shipRowsHorizontal = vertical.slice(vertical.indexOf(pointerRow) - 1, (vertical.indexOf(pointerRow) + 2));
            for(let i = (pointerCol - 1); i < (pointerCol + shipDO[currentShipType] + 1); i++){
                for(let j = 0; j < shipRowsHorizontal.length; j++){
                    currentShadow.push(shipRowsHorizontal[j] + i);
                    addPlacingShipShadowClass(shipRowsHorizontal[j] + i);
                }
            }
            break;
        case 'portrait':
            let shipRows = vertical.slice(vertical.indexOf(pointerRow) - 1, (vertical.indexOf(pointerRow) + shipDO[currentShipType] + 1));
            for(let i = 0; i < shipRows.length; i++){
                for(let j = (pointerCol - 1); j < (pointerCol + 2); j++){
                    currentShadow.push(shipRows[i] + j);
                    addPlacingShipShadowClass(shipRows[i] + j);
                }
            }
            break;
    }

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

        $elementWithID.addClass('savedShip')
            .data("info", {"shipType": shipType,
                "location": id});
    });
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
