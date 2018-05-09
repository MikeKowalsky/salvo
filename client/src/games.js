import React, { Component } from 'react';
import './Games.css';

class Games extends Component<> {

    constructor() {
        super();

        this.state = {
            games: [],
            isLoading: true
        };
    }

    componentDidMount() {

        fetch('http://localhost:8080/api/games')
            .then(response => response.json())
            .then(data => {
                console.log(data);
                this.setState({games: data, isLoading: false});
            });
    }

    render() {
        const {games, isLoading} = this.state;

        if (isLoading) {
            return <p>Loading...</p>;
        }

        console.log(this.state.games);

        return (
            <div>
                <div className={'gameList'}>
                    <h2>Game List</h2>
                    {games.map((game) =>
                        <div className={'gameListElement'} key={game.id}>
                            Game ID: {game.id}&nbsp;
                            Created: {new Date(game.created).toString()}&nbsp;
                            Player One: {game.gamePlayers[0].player.email}&nbsp;
                            Player Two: {(game.gamePlayers.length > 1) ? game.gamePlayers[1].player.email : "N/A"}&nbsp;
                        </div>
                    )}
                </div>
            </div>
        );
    }

}

export default Games;
