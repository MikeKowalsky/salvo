import React, { Component } from 'react';

import Grid from './Grid';

import './Game.css';

class GamePlayerId extends Component<> {

    constructor() {
        super();

        this.state = {
            oneGame: {},
            isLoading: true,
            gamePlayerId: ''
        };


    }

    componentDidMount() {

        // const {gamePlayerId} = this.props.match.params;
        // console.log(gamePlayerId);

        let vars = [], hash;
        let hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
        for(let i = 0; i < hashes.length; i++) {
            hash = hashes[i].split('=');
            vars[hash[0]] = hash[1];
        }
        console.log(vars);

        let url = "http://localhost:8080/api/game_view/" + vars.gp;
        console.log(url);

        fetch(url)
            .then(response => response.json())
            .then(data => {
                console.log(data);
                this.setState({oneGame: data, isLoading: false, gamePlayerId: vars.gp});

            });
    }

    render() {
        const {oneGame, isLoading, gamePlayerId} = this.state;

        if (isLoading) {
            return <p>Loading...</p>;
        }

        return (
            <div>
                <div className={'gameChosen'}>
                    <h2>Game</h2>
                    <div className={'bold'}>Game ID: {oneGame.gameId}</div>
                    <div>Created: {new Date(oneGame.created).toString()}</div>
                    {(oneGame.gamePlayers[0].id.toString() === gamePlayerId) ?
                        <div className={'bold'}>Player One(you): {oneGame.gamePlayers[0].player.email}</div> :
                        <div>Player One: {oneGame.gamePlayers[0].player.email}</div>
                    }
                    {(oneGame.gamePlayers[1].id.toString() === gamePlayerId) ?
                        <div className={'bold'}>Player Two (you): {oneGame.gamePlayers[1].player.email}</div> :
                        <div>Player Two: {oneGame.gamePlayers[1].player.email}</div>
                    }
                </div>

                <table className={'gridTable'}>
                    <Grid {...this.state.oneGame.ships}/>
                </table>

            </div>
        );
    }


}

export default GamePlayerId;
