import React from 'react';
import './App.css';
import {BrowserRouter as Router} from "react-router-dom";
import {RoutesComponents} from "./router/Routes/index";
import {AccountStateContextProvider} from "./context/AccountContext";



function App() {

  return (
      <AccountStateContextProvider>
          <Router>
              <RoutesComponents/>
          </Router>
      </AccountStateContextProvider>
  )
}

export default App;
