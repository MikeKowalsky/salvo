
function activateSalvosPlacingButton(){

    $('#activateSalvoePlacing').on('click', function(){
        handleOnMouseOverSalvoes();
        $('#activateSalvoePlacing').prop('disabled', true);
    })
}


function handleOnMouseOverSalvoes() {

    let vertical = ['','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'];
    let IDs = buildGridIdsArray(vertical, 'forSalvos');

    $('#salvoGrid').on('mouseover', function(e){

        $('#salvoGrid').off("click");

        $('#salvoGrid *').removeClass('placingSalvo notAllowedSalvo');

        if (IDs.includes(e.target.id)) {
            // console.log(e.target.id);

            let $currentID = $('#' + e.target.id);
            if ($currentID.hasClass('playersSalvo')){
                $currentID.addClass('notAllowedSalvo');
            } else {
                $currentID.addClass('placingSalvo');

                // storing salvoes, counting them, activating onclick mouse e
                let thisTurnSalvosArray = currentTurnSalvoesArray();
                if (thisTurnSalvosArray.length < 5){

                    //empty array, hide button, switch off event listener
                    $('#locationArray').empty();
                    $('#saveLocationDiv').hide();
                    $('#saveLocationButton').off('click');

                    handleOnClickSalvoes(IDs);
                } else if (thisTurnSalvosArray.length === 5){
                    // console.log(thisTurnSalvosArray);
                    $('#locationArray').empty().append('Your salvo: ' + thisTurnSalvosArray);
                    showButtonAndHandleSendingSalvoesData(thisTurnSalvosArray);
                }

                // activate 'dragging' a salvo
                if(thisTurnSalvosArray.length > 0 && $currentID.hasClass('savedSalvo')){
                    handleDraggingSalvoes();
                }
            }
        }
    })
}

function handleOnClickSalvoes(IDs) {

    $('#salvoGrid').on('click', function(e){
        if (IDs.includes(e.target.id)) {
            console.log("clicked: " + e.target.id);
            $('#' + e.target.id).addClass('savedSalvo');
        }
    })
}

function handleDraggingSalvoes() {

    $('#salvoGrid').on('click', function (e) {
        $('#' + e.target.id).removeClass('savedSalvo');
    })
}

function currentTurnSalvoesArray() {
    let currentSalvoesArray = [];

    $('.savedSalvo').each(function(){
        currentSalvoesArray.push(this.id.substr(1));
    });

    // console.log(currentSalvoesArray);
    return currentSalvoesArray;
}