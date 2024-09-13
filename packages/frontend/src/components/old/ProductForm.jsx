// src/components/ProductForm.js
import React, { useState } from 'react';
import axios from 'axios';

const ProductForm = ({ isOpen, onClose, onProductSubmit, initialProduct = {}, defaultUrl }) => {
	const [name, setName] = useState(initialProduct.name || '');
	const [price, setPrice] = useState(initialProduct.price || '');
	const [description, setDescription] = useState(initialProduct.description || '');
	const [error, setError] = useState('');

	const handleSubmit = async () => {
		try {
			if (initialProduct.id) {
				await axios.put(`${defaultUrl}/${initialProduct.id}`, { name, price, description });
			} else {
				await axios.post(defaultUrl, { name, price, description });
			}
			onProductSubmit(); // Notify parent component
			onClose(); // Close the form
		} catch (err) {
			setError('Failed to submit product. Please try again.');
		}
	};

	if (!isOpen) return null;

	return (
		<div>
			<h2>{initialProduct.id ? 'Edit Product' : 'Create Product'}</h2>
			<input type='text' placeholder='Name' value={name} onChange={(e) => setName(e.target.value)} />
			<input type='number' placeholder='Price' value={price} onChange={(e) => setPrice(e.target.value)} />
			<textarea placeholder='Description' value={description} onChange={(e) => setDescription(e.target.value)} />
			<button onClick={handleSubmit}>{initialProduct.id ? 'Submit' : 'Create Product'}</button>
			<button onClick={onClose}>Close Form</button>
			{error && <div>{error}</div>}
		</div>
	);
};

export default ProductForm;
