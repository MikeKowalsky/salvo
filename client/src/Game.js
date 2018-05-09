import React, { Component } from 'react';

import Grid from './Grid';

import './Game.css';

class Game extends Component<> {

    constructor() {
        super();

        this.state = {
            oneGame: {},
            isLoading: true
        };
    }

    componentDidMount() {

        fetch("http://localhost:8080/api/game_view/1")
            .then(response => response.json())
            .then(data => {
                console.log(data);
                this.setState({oneGame: data, isLoading: false});

            });
    }

    render() {
        const {oneGame, isLoading} = this.state;

        if (isLoading) {
            return <p>Loading...</p>;
        }

        console.log(this.state.oneGame);

        return (
            <div>
                <div className={'gameChosen'}>
                    <h2>Game</h2>
                    <div>Game ID: {oneGame.gameId}</div>
                    <div>Created: {new Date(oneGame.created).toString()}</div>
                    <div>Player One: {oneGame.gamePlayers[0].player.email}</div>
                    <div>Player Two: {oneGame.gamePlayers[1].player.email}</div>
                </div>

                <table className={'gridTable'}>
                    <Grid {...this.state.oneGame.ships}/>
                </table>

            </div>
        );
    }


}

export default Game;
