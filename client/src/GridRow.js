import React from 'react';

// import './Header.css';


function GridRow({rowName}) {

    let columns = [];

    for(let c = 0; c < 11; c++){
        if (rowName === '' & c !== 0){
            columns.push(<td key={c}>{c}</td>);
        } else if (c === 0){
            columns.push(<td key={c}>{rowName}</td>);
        } else {
            columns.push(<td key={c}/>);
        }
    }

    return <tr>{columns}</tr>;
}

export default GridRow;
