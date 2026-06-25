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
    <div className="login-page-wrapper">
      <div className="login-left-panel">
        <div className="login-left-header">
          <span className="logo-queue">Queue</span>
          <span className="logo-cure">Cure</span>
        </div>

        <div className="login-box">
          <h1 className="login-greeting">Hello.</h1>
          <p className="login-greeting-desc">
            Please enter your receptionist credentials to access the queue dashboard.
          </p>

          <form onSubmit={handleSubmit} className="login-form">
            <div className="form-group">
              <label htmlFor="username">Username</label>
              <div style={{ position: 'relative' }}>
                <FaUserMd className="input-icon-left" />
                <input
                  type="text"
                  id="username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="Enter username"
                  disabled={loading}
                />
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="password">Password</label>
              <div style={{ position: 'relative' }}>
                <FaLock className="input-icon-left" />
                <input
                  type="password"
                  id="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Enter password"
                  disabled={loading}
                />
              </div>
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

        <div className="login-left-footer">
          &copy; {new Date().getFullYear()} QueueCure. All rights reserved.
        </div>
      </div>

      <div className="login-right-panel">
        <div className="login-right-glass-card">
          <div className="glass-card-logo">
            <FaUserMd style={{ fontSize: '1.8rem', color: '#ffffff' }} />
            <span style={{ fontSize: '1.4rem', fontWeight: 800, color: '#ffffff', letterSpacing: '-0.5px' }}>
              QueueCure
            </span>
          </div>
          <h2 className="glass-card-title">
            Precision clinic queueing is the new gold standard for patient satisfaction.
          </h2>
          <p className="glass-card-text">
            Manage check-ins, track patient status, and streamline receptionist workflows with our responsive real-time dashboard.
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;