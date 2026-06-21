import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FaUserMd, FaLock, FaSignInAlt } from 'react-icons/fa';
import { toast } from 'react-toastify';
import '../styles/login.css';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  // Hardcoded credentials for demo
  const VALID_CREDENTIALS = {
    username: 'receptionist',
    password: 'clinic123'
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (!username.trim() || !password.trim()) {
      toast.warning('Please enter both username and password');
      return;
    }

    setLoading(true);

    // Simulate authentication
    setTimeout(() => {
      if (username === VALID_CREDENTIALS.username && password === VALID_CREDENTIALS.password) {
        toast.success('Login successful!');
        // Store login state
        localStorage.setItem('isLoggedIn', 'true');
        localStorage.setItem('username', username);
        navigate('/receptionist');
      } else {
        toast.error('Invalid username or password');
        setLoading(false);
      }
    }, 500);
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <div className="login-header">
          <FaUserMd className="login-icon" />
          <h1>Queue Solved</h1>
          <p>Receptionist Login</p>
        </div>

        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label htmlFor="username">
              <FaUserMd className="input-icon" />
              Username
            </label>
            <input
              type="text"
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Enter username"
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">
              <FaLock className="input-icon" />
              Password
            </label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter password"
              disabled={loading}
            />
          </div>

          <button type="submit" className="btn-login" disabled={loading}>
            <FaSignInAlt />
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>

        <div className="login-footer">
          <p>Demo credentials: receptionist / clinic123</p>
        </div>
      </div>
    </div>
  );
};

export default Login;