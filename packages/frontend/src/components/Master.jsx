import React, { useState, useEffect } from 'react';
import axios from 'axios';

// Combined Component: LoginForm and ProductTable
const LoginAndProductTable = () => {
	const [username, setUsername] = useState('');
	const [password, setPassword] = useState('');
	const [isLoggedIn, setIsLoggedIn] = useState(false);
	const [isAdmin, setIsAdmin] = useState(false);
	const [error, setError] = useState('');
	const [products, setProducts] = useState([]);
	const [isFormOpen, setIsFormOpen] = useState(false);
	const [editProduct, setEditProduct] = useState(null);

	const apiUrl = 'http://localhost:8080/api';
	const urlProducts = `${apiUrl}/products`;
	const urlUsers = `${apiUrl}/users`;

	// Handle user login
	const handleLogin = async () => {
		setError('');
		if (!username || !password) {
			setError('Username and password cannot be empty.');
			return;
		}
		try {
			const response = await axios.post(`${urlUsers}/login`, { username, password });
			setIsLoggedIn(true);
			setIsAdmin(response.data.role === 'ADMIN'); // Get admin role from login response
			setUsername('');
			setPassword('');
			fetchProducts(); // Fetch products after login
		} catch (err) {
			setError('Login failed. Please try again.');
		}
	};

	// Handle user logout
	const handleLogout = async () => {
		try {
			await axios.post(`${urlUsers}/logout`);
			setIsLoggedIn(false);
			setIsAdmin(false);
			fetchProducts(); // Refetch products after logout
		} catch (err) {
			setError('Logout failed. Please try again.');
		}
	};

	// Fetch user role (admin or not) from the /users/current endpoint
	const fetchUserRole = async () => {
		try {
			const response = await axios
				.get(`${urlUsers}/current`, { withCredentials: true })
				.then((response) => console.log(response.data))
				.catch((error) => console.error('Error fetching current user:', error));

			setIsAdmin(response.data.role === 'ADMIN');
		} catch (err) {
			console.error('Failed to fetch user role:', err);
		}
	};

	// Fetch products based on user role and login status
	const fetchProducts = async () => {
		try {
			let response = await axios.get(urlProducts);
			if (isAdmin) {
				response = await axios.get(`${urlProducts}/pending`);
				setProducts(response.data);
			} else {
				setProducts(response.data);
			}
		} catch (err) {
			console.error('Failed to fetch products:', err);
		}
	};

	// Handle product deletion
	const handleDelete = async (id) => {
		try {
			await axios.delete(`${urlProducts}/${id}`);
			fetchProducts(); // Refetch products after deletion
		} catch (err) {
			alert('Failed to delete product.');
		}
	};

	// Handle product approval
	const handleApprove = async (id) => {
		try {
			await axios.put(`${urlProducts}/${id}/approve`);
			fetchProducts(); // Refetch products after approval
		} catch (err) {
			alert('Failed to approve product.');
		}
	};

	// Handle product rejection
	const handleReject = async (id) => {
		try {
			await axios.put(`${urlProducts}/${id}/reject`);
			fetchProducts(); // Refetch products after rejection
		} catch (err) {
			alert('Failed to reject product.');
		}
	};

	// Handle product form submission (create or update)
	const handleProductSubmit = () => {
		setIsFormOpen(false);
		fetchProducts(); // Refetch products after form submission (create or update)
	};

	// Effect to fetch products on component mount and check user role
	useEffect(() => {
		fetchProducts(); // Fetch products initially
		if (isLoggedIn) {
			fetchUserRole(); // Fetch user role if logged in
		}
	}, [isLoggedIn, isAdmin]);

	return (
		<div>
			<div style={{ textAlign: 'right' }}>
				{isLoggedIn ? (
					<>
						<span>Welcome, {username}</span>
						<button onClick={handleLogout}>Logout</button>
					</>
				) : (
					<>
						<div className='form-inline'>
							<input type='text' placeholder='Username' value={username} onChange={(e) => setUsername(e.target.value)} />
							<input type='password' placeholder='Password' value={password} onChange={(e) => setPassword(e.target.value)} />
							<button onClick={handleLogin}>Login</button>
						</div>
						{error && <div style={{ color: 'red' }}>{error}</div>}
					</>
				)}
			</div>

			{isLoggedIn && (
				<>
					<button onClick={() => setIsFormOpen(true)}>Create Product</button>
					{isFormOpen && <ProductForm isOpen={isFormOpen} onClose={() => setIsFormOpen(false)} onProductSubmit={handleProductSubmit} initialProduct={editProduct} defaultUrl={urlProducts} />}
				</>
			)}

			<table style={{ marginTop: '2rem' }}>
				<thead>
					<tr>
						<th>Name</th>
						<th>Price</th>
						<th>Description</th>
						{isLoggedIn && <th>Status</th>}
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					{products.length ? (
						products.map((product) => (
							<tr key={product.id}>
								<td>{product.name}</td>
								<td>{product.price}</td>
								<td>{product.description}</td>
								{isLoggedIn && <td>{product.status}</td>}
								<td>
									{isLoggedIn && !isAdmin && (
										<>
											<button
												onClick={() => {
													setEditProduct(product);
													setIsFormOpen(true);
												}}
											>
												Edit
											</button>
											<button onClick={() => handleDelete(product.id)}>Delete</button>
										</>
									)}

									{isAdmin && (
										<>
											<button onClick={() => handleApprove(product.id)}>Approve</button>
											<button onClick={() => handleReject(product.id)}>Reject</button>
										</>
									)}
								</td>
							</tr>
						))
					) : (
						<tr>
							<td colSpan='5'>No products available.</td>
						</tr>
					)}
				</tbody>
			</table>
		</div>
	);
};

// ProductForm Component
const ProductForm = ({ isOpen, onClose, onProductSubmit, initialProduct, defaultUrl }) => {
	const [name, setName] = useState(initialProduct?.name || '');
	const [price, setPrice] = useState(initialProduct?.price || '');
	const [description, setDescription] = useState(initialProduct?.description || '');

	const handleSubmit = async () => {
		try {
			if (initialProduct) {
				// Edit product
				await axios.put(`${defaultUrl}/${initialProduct.id}`, { name, price, description });
			} else {
				// Create new product
				await axios.post(defaultUrl, { name, price, description });
			}
			onProductSubmit();
		} catch (err) {
			alert('Failed to submit product.');
		}
	};

	return isOpen ? (
		<div className='form-inline'>
			<input type='text' placeholder='Product Name' value={name} onChange={(e) => setName(e.target.value)} />
			<input type='text' placeholder='Price' value={price} onChange={(e) => setPrice(e.target.value)} />
			<input type='text' placeholder='Description' value={description} onChange={(e) => setDescription(e.target.value)} />
			<button onClick={handleSubmit}>Submit</button>
			<button onClick={onClose}>Cancel</button>
		</div>
	) : null;
};

export default LoginAndProductTable;
