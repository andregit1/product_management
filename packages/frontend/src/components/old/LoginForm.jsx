import React, { useState } from 'react';
import axios from 'axios';
// import { useNavigate } from 'react-router-dom'; // Using react-router for navigation

const LoginForm = ({ onLogin }) => {
	const [username, setUsername] = useState('');
	const [password, setPassword] = useState('');
	const [isLoggedIn, setIsLoggedIn] = useState(false);
	const [error, setError] = useState('');
	const defaultUrl = 'http://localhost:8080/api/users';

	// const navigate = useNavigate(); // To handle navigation

	const handleLogin = async () => {
		// Clear previous error
		setError('');

		// Validation for empty fields
		if (!username || !password) {
			setError('Username and password cannot be empty.');
			return;
		}

		try {
			// Make a request to your login API
			await axios.post(`${defaultUrl}/login`, { username, password });

			// Simulate successful login
			setIsLoggedIn(true);
			onLogin(true); // Notify parent component

			// Clear the form fields
			setUsername('');
			setPassword('');

			// Redirect to ProductTable page
			// navigate('/products');
		} catch (err) {
			setError('Login failed. Please try again.');
		}
	};

	const handleLogout = async () => {
		try {
			await axios.post(`${defaultUrl}/logout`); // Logout request
			setIsLoggedIn(false);
			onLogin(false);
			// navigate('/'); // Uncomment if you use it
		} catch (err) {
			setError('Logout failed. Please try again.');
		}
	};

	return (
		<div>
			<div style={{ textAlign: 'right' }}>
				{isLoggedIn ? (
					<>
						<span>Welcome, {username}</span>
						<button onClick={handleLogout}>Logout</button>
					</>
				) : (
					// Show the login form when the user is not logged in
					<div>
						<input type='text' placeholder='Username' value={username} onChange={(e) => setUsername(e.target.value)} />
						<input type='password' placeholder='Password' value={password} onChange={(e) => setPassword(e.target.value)} />
						<button onClick={handleLogin}>Login</button>
						{error && <div style={{ color: 'red' }}>{error}</div>}
					</div>
				)}
			</div>
		</div>
	);
};

export default LoginForm;
