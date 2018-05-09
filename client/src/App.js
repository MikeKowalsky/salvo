import React, { Component } from 'react';
import {BrowserRouter as Router, Route, Link, Switch} from "react-router-dom";

import Home from './Home';
import Games from './Games';

import logo from './logo.png';
import './App.css';

class App extends Component<> {

  // constructor() {
  //     super();
  // }


  render() {

      return (
          <div className="App">
              <header className="App-header">
                  <img src={logo} className="App-logo" alt="logo" />
                  <h1 className="App-title">Welcome to Salvo!</h1>
              </header>
              <div>

              </div>

              <Router>
                  <div>
                      <ul>
                          <li>
                              <Link to="/">Home</Link>
                          </li>
                          <li>
                              <Link to="/games">Games</Link>
                          </li>
                      </ul>

                      <Switch>
                            <Route exact path="/" component={Home} />
                            <Route path="/games" component={Games} />
                      </Switch>
                  </div>
              </Router>

          </div>

      );
  }

}

export default App;
