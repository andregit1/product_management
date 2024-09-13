// src/components/ProductTable.js
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import ProductForm from './ProductForm';

const ProductTable = ({ isLoggedIn, isAdmin }) => {
	const [products, setProducts] = useState([]);
	const [isFormOpen, setIsFormOpen] = useState(false);
	const [editProduct, setEditProduct] = useState(null);
	const urlProducts = `http://localhost:8080/api/products`;

	// Fetch products from the API
	useEffect(() => {
		const fetchProducts = async () => {
			try {
				const response = await axios.get(urlProducts);
				setProducts(response.data);
			} catch (err) {
				console.error('Failed to fetch products:', err);
			}
		};
		fetchProducts();
	}, []);

	// Handle product deletion
	const handleDelete = async (id) => {
		try {
			await axios.delete(`${urlProducts}/${id}`);
			setProducts(products.filter((product) => product.id !== id));
		} catch (err) {
			alert('Failed to delete product.');
		}
	};

	// Handle product approval
	const handleApprove = async (id) => {
		try {
			await axios.put(`${urlProducts}/${id}/approve`);
			setProducts(products.map((product) => (product.id === id ? { ...product, status: 'approved' } : product)));
		} catch (err) {
			alert('Failed to approve product.');
		}
	};

	// Handle product rejection
	const handleReject = async (id) => {
		try {
			await axios.put(`${urlProducts}/${id}/reject`);
			setProducts(products.map((product) => (product.id === id ? { ...product, status: 'rejected' } : product)));
		} catch (err) {
			alert('Failed to reject product.');
		}
	};

	return (
		<div>
			{/* Conditionally show the product form for creating or editing a product */}
			{isLoggedIn && (
				<>
					<button onClick={() => setIsFormOpen(true)}>Create Product</button>
					<ProductForm
						isOpen={isFormOpen}
						onClose={() => setIsFormOpen(false)}
						onProductSubmit={() => {
							setIsFormOpen(false);
							// Optionally reload the products after form submission
						}}
						initialProduct={editProduct}
						defaultUrl={urlProducts}
					/>
				</>
			)}

			<table>
				<thead>
					<tr>
						<th>Name</th>
						<th>Price</th>
						<th>Description</th>
						{isLoggedIn && <th>Status</th>} {/* Show Status only if logged in */}
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					{products.map((product) => (
						<tr key={product.id}>
							<td>{product.name}</td>
							<td>{product.price}</td>
							<td>{product.description}</td>
							{isLoggedIn && <td>{product.status}</td>}
							<td>
								{/* Regular users (non-admin) can edit or delete products if logged in */}
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

								{/* Admin users can approve or reject products */}
								{isAdmin && (
									<>
										<button onClick={() => handleApprove(product.id)}>Approve</button>
										<button onClick={() => handleReject(product.id)}>Reject</button>
									</>
								)}
							</td>
						</tr>
					))}
				</tbody>
			</table>
		</div>
	);
};

export default ProductTable;
