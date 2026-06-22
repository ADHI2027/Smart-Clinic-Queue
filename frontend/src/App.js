import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './App.css';
import ReceptionistDashboard from './components/ReceptionistDashboard';
import TVDisplay from './components/TVDisplay';
import Login from './components/Login';
import SelfRegistration from './components/SelfRegistration';

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/receptionist" element={<ReceptionistDashboard />} />
          <Route path="/tv" element={<TVDisplay />} />
          <Route path="/self-register" element={<SelfRegistration />} />
          <Route path="/" element={<Navigate to="/login" />} />
        </Routes>
        <ToastContainer 
          position="top-right"
          autoClose={3000}
          hideProgressBar={false}
          newestOnTop
          closeOnClick
          rtl={false}
          pauseOnFocusLoss
          draggable
          pauseOnHover
          theme="light"
        />
      </div>
    </Router>
  );
}

export default App;