import React, { Component } from 'react';

class Games extends Component<> {

    constructor() {
        super();

        this.state = {
            games: [],
            isLoading: false
        };
    }

    componentDidMount() {
        this.setState({isLoading: true});

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

        console.log(games);

        let copyState = [...this.state.games];

        copyState.map((game) => game.created = new Date(game.created).toString());

        return (
            <div>
                <div>
                    <h2>Game List</h2>
                    {copyState.map((game) =>
                        <div key={game.id}>
                            Game ID: {game.id}
                            Created: {game.created}
                            Player One: {game.gamePlayers[0].player.email}
                            Player Two: {(game.gamePlayers.length > 1) ? game.gamePlayers[1].player.email : "N/A"}
                        </div>
                    )}
                </div>
            </div>
        );
    }

}

export default Games;
