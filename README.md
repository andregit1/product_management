# Product Management System

## Overview

This project is a product management system with user authentication and product approval workflows. It consists of a Spring Boot backend and a React.js frontend.

**Technologies Used:**

- **Backend:** Spring Boot, JPA, Liquibase, MySQL 8, Lombok
- **Frontend:** React.js
- **Database:** MySQL 8

**Environment:**

- **OS:** Linux (Pop!\_OS 22.04)
- **JDK:** OpenJDK 21

## Setup and Installation

### Running Both Backend and Frontend

To install both the backend and frontend:

```bash
npm run initiate
```

To start both the backend and frontend:

```bash
npm run dev
```

The backend API documentation can be accessed at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).

The frontend will be available at [http://localhost:3000](http://localhost:3000).

### Backend Setup

1. **Install OpenJDK 21**

   - Follow the instructions for installing OpenJDK 21 on your Linux distribution.

2. **Clone the Repository**

   ```bash
   git clone https://github.com/andregit1/product_management.git
   cd product_management
   ```

3. **Build and Run the Backend**

   ```bash
   cd packages/backend && mvn spring-boot:run
   ```

4. **Database Configuration**

   - Configure MySQL 8 and ensure it's running.
   - Update the `application.properties` with your MySQL configuration.

5. **Seed Data**
   - The initial data includes:
     - **Admin User:** Username: `admin`, Password: `123123123`, Role: `ADMIN`
     - **Member User:** Username: `demoUser0`, Password: `123123123`, Role: `MEMBER`
   - Products:
     - 10 products total
     - 5 approved
     - 5 pending
     - 5 rejected

### Frontend Setup

1. **Install Dependencies**

   ```bash
   cd packages/frontend
   npm install
   ```

2. **Start the Development Server**

   ```bash
   npm start
   ```

   The frontend will be accessible at `http://localhost:3000`.

## API Endpoints

### User Management

- **POST** `/api/users/register`

  - Create a new user

- **POST** `/api/users/logout`

  - User logout

- **POST** `/api/users/login`

  - User login

- **GET** `/api/users`

  - Get all users

- **GET** `/api/users/current`
  - Get current user

### Product Management

- **POST** `/api/products`

  - Add a new product

- **GET** `/api/products`

  - Get all approved products

- **GET** `/api/products/pending`

  - Get pending products

- **PUT** `/api/products/{id}`

  - Update product

- **DELETE** `/api/products/{id}`

  - Delete product

- **PUT** `/api/products/{id}/reject`

  - Reject product

- **PUT** `/api/products/{id}/approve`
  - Approve product

## Application Flow

1. **Without Login:**

   - Users can see all approved products.

2. **Logged-in Member:**

   - Can see all product statuses.
   - Can create, update, and delete their own products.

3. **Logged-in Admin:**

   - Can see only pending products.
   - Can approve or reject products.

4. **Editing and Deletion:**
   - Rejected products can be edited and their status changed to pending.
   - Deleted products are permanently removed.

## Table Relations

- **User** has many **Products**
- **Product** belongs to a **User**
  - **checked_by**: Admin who approved or rejected the product
  - **created_by**: Member who created the product
