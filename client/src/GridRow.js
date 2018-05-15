import React from 'react';

function GridRow({rowName, rowArray}) {

    let columns = [];

    for(let c = 0; c < 11; c++){
        if (rowName === '' & c !== 0){
            columns.push(<td key={c}>{c}</td>);
        } else if (c === 0){
            columns.push(<td key={c}>{rowName}</td>);
        } else {
            if(rowArray !== undefined && rowArray.includes(c.toString())){
                columns.push(<td key={c} id={rowName + c} className={'playersShip'}/>);
            } else {
                columns.push(<td key={c} id={rowName + c}/>);
            }
        }
    }

    return <tr>{columns}</tr>;
}

export default GridRow;
