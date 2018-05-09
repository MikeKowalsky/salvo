import React, {Component} from 'react';

import GridRow from './GridRow';

import './Grid.css';


class Grid extends Component<> {

    // constructor() {
    //     super();
    //
    //     this.state = {
    //         oneGame: {},
    //         isLoading: true
    //     };
    // }

    render(){

        let shipLocationsFromParentElement = this.props;
        console.log(shipLocationsFromParentElement);
        console.log(typeof(shipLocationsFromParentElement));

        for(let property in shipLocationsFromParentElement){
            console.log(property);
        }

        let rows = [];
        let vertical = ['','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J']

        for (let r = 0; r < 11; r++) {
            rows.push(<GridRow rowName={vertical[r]} key={r}/>);
        }
        return <tbody className={'Grid'}>{rows}</tbody>;
    }
}

export default Grid;