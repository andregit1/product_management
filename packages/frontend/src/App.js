import './Product.css';
import React from 'react';
import LoginAndProductTable from './components/Master';
import axios from 'axios';

// Enable sending cookies with every request
axios.defaults.withCredentials = true;

const App = () => {
  return (
    <div className="container">
      <h1 className="App-header">Product Management System</h1>
      <LoginAndProductTable />
    </div>
  );
};

export default App;
