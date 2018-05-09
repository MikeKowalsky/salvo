import React from 'react';

import logo from './logo.png';

import './Header.css';


function Header() {
        return (
            <div className={'header'}>
                <img src={logo} className="logo" alt="logo" />
                <h1 className="title">Welcome to Salvo!</h1>
            </div>
        );
}

export default Header;



