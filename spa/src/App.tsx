import React from 'react';
import './App.css';
import {BrowserRouter as Router} from "react-router-dom";
import {RoutesComponents} from "./router/Routes/index";



function App() {

  return (
      <Router>
        <RoutesComponents/>
      </Router>
  )
}

export default App;
