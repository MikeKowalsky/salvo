import React, { Component } from 'react';
import {BrowserRouter as Router, Route, Link, Switch} from "react-router-dom";

import Header from './Header';
import Home from './Home';
import Games from './Games';
// import Game from './Game';
import GamePlayerId from './GamePlayerId';

import './App.css';

class App extends Component<> {

  // constructor() {
  //     super();
  // }


  render() {

      return (
          <div className="App">

              <Header/>

              <Router>
                  <div>
                      <ul className={'App-menu'}>
                          <li>
                              <Link to="/">Home</Link>
                          </li>
                          <li>
                              <Link to="/games">Games</Link>
                          </li>
                          <li>
                              <Link to="/game/?gp=1">Game</Link>
                          </li>
                          {/*<li>*/}
                              {/*<Link to={{*/}
                                  {/*pathname: "/game",*/}
                                  {/*search: "?gp=:gamePlayerId"*/}
                              {/*}}>GamePlayerID</Link>*/}
                          {/*</li>*/}
                      </ul>

                      <Switch>
                          <Route exact path="/" component={Home} />
                          <Route path="/games" component={Games} />
                          {/*<Route path="/game" component={Game} />*/}
                          <Route path="/game" component={GamePlayerId} />
                      </Switch>
                  </div>
              </Router>

          </div>

      );
  }

}

export default App;
