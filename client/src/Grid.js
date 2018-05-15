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

        let shipLocationMainArray =[];

        //changing into one array and sort
        for (const key of Object.keys(shipLocationsFromParentElement)) {
            // console.log(key, shipLocationsFromParentElement[key].locations);
            shipLocationsFromParentElement[key].locations.forEach(
                (location) => shipLocationMainArray.push(location));
        }
        shipLocationMainArray.sort();

        //new object to mark (add class) to indexes during rendering
        let shipLocationsObject = {};
        shipLocationMainArray.forEach((location) =>
            shipLocationsObject[location.charAt(0)] = []);
        shipLocationMainArray.forEach((location) =>
            shipLocationsObject[location.charAt(0)].push(location.charAt(1)));
        console.log(shipLocationsObject);

        let rows = [];
        let vertical = ['','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J']


        for (let r = 0; r < 11; r++) {
            if (shipLocationsObject.hasOwnProperty(vertical[r])){
                rows.push(<GridRow rowName={vertical[r]} rowArray={shipLocationsObject[vertical[r]]} key={r}/>);
            } else {
                rows.push(<GridRow rowName={vertical[r]} key={r}/>);
            }
        }

        return <tbody className={'Grid'}>{rows}</tbody>;
    }
}

export default Grid;