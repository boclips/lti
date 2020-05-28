import React from 'react';
import './App.css';
import { AuthService } from './service/AuthService';

function App(): React.ReactElement {
  AuthService.configureAxios();

  return (
    <div className="App">
      <header className="App-header">
        <p>Hello, World!</p>
      </header>
    </div>
  );
}

export default App;
