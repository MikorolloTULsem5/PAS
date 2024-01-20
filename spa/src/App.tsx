import React, {useEffect} from 'react';
import './App.css';
import {adminsApi} from "./api/adminsApi";
import {BrowserRouter as Router} from "react-router-dom";
import {RoutesComponents} from "./router/Routes/index";



function App() {

  useEffect(() => {
     console.log(adminsApi.getAdmins());
  }, []);

  return (
      <Router>
        <RoutesComponents/>
      </Router>
  )
}

export default App;
