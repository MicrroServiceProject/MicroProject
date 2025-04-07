# MicroProject - E-commerce Backend

This project is a Spring Boot application providing the backend RESTful API for a simple e-commerce platform, likely designed to interact with a frontend like Angular (judging by the `@CrossOrigin` annotation). It manages products, users, shopping carts, orders, and user favorites. It also includes features for sending order confirmation emails with attached PDF invoices.

## Features

*   **Product Management:**
    *   CRUD operations (Create, Read, Update, Delete) for products.
    *   Search products by name (case-insensitive).
    *   List all available products.
*   **User Management:**
    *   User creation with username and email.
*   **Shopping Cart:**
    *   Associate a persistent cart with each user.
    *   Add items to the cart.
    *   Update item quantity in the cart.
    *   Remove items from the cart.
    *   Clear the entire cart.
    *   View cart contents and total amount.
*   **Order Management:**
    *   Place orders based on items (likely intended to be populated from the cart).
    *   View order history for a specific user.
    *   Track order status (Pending, Processing, etc.).
*   **Favorites Management:**
    *   Allow users to add products to their favorites list.
    *   Allow users to remove products from their favorites list.
    *   View a user's favorite products.
*   **Notifications:**
    *   Asynchronously send order confirmation emails upon successful order placement.
    *   Generate and attach a PDF invoice to the confirmation email.
*   **API:**
    *   RESTful endpoints for all major functionalities.

## Technologies Used

*   **Backend:** Java 17, Spring Boot 3.2.3
    *   Spring Web: For building REST APIs.
    *   Spring Data JPA: For database interaction using Hibernate.
    *   Spring Mail: For sending emails.
    *   Spring Boot Starter Thymeleaf: For processing HTML email templates.
*   **Database:** MySQL 8
*   **PDF Generation:** OpenPDF
*   **Build Tool:** Maven
*   **Other:**
    *   Lombok (used in some DTOs for boilerplate code reduction, although many entities/DTOs use manual getters/setters).
    *   Jackson Datatype Hibernate6: For handling lazy-loaded entities during JSON serialization.

## Prerequisites

Before you begin, ensure you have met the following requirements:

*   **Java Development Kit (JDK):** Version 17 or higher.
*   **Maven:** Build automation tool.
*   **MySQL Server:** Version 8 or compatible.
*   **Git:** Version control system (optional, for cloning).
*   **IDE:** An IDE like IntelliJ IDEA, Eclipse, or VS Code is recommended.

## Setup and Installation

1.  **Clone the repository:**
    ```bash
    git clone <your-repository-url>
    cd MicroProject
    ```

2.  **Configure Database:**
    *   Open the `src/main/resources/application.properties` file.
    *   Ensure the MySQL server is running.
    *   Update the `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` properties to match your MySQL setup. The application is configured to create the database (`artshopdb`) if it doesn't exist (`createDatabaseIfNotExist=true`).
    *   The `spring.jpa.hibernate.ddl-auto=update` setting will attempt to update the database schema based on your entities. **Note:** This is convenient for development but not recommended for production environments.

3.  **Configure Email (Gmail Example):**
    *   Open `src/main/resources/application.properties`.
    *   Update `spring.mail.username` with your Gmail address (`no.reply.davincci@gmail.com` is currently set).
    *   **Important:** For `spring.mail.password`, you need to generate a **Google App Password** if you have 2-Step Verification enabled on the Gmail account. Do **not** use your regular Gmail password here.
        *   Go to your Google Account -> Security -> 2-Step Verification -> App passwords.
        *   Generate a new App Password for "Mail" on "Other (Custom name)" (e.g., "SpringBoot MicroProject").
        *   Copy the generated 16-character password and paste it into the `spring.mail.password` property.
    *   Ensure the `spring.mail.properties.mail.from` property (if you add it) matches the `spring.mail.username`.

## Running the Application

You can run the application in several ways:

1.  **Using Maven Wrapper:**
    ```bash
    # On Linux/macOS
    ./mvnw spring-boot:run

    # On Windows
    .\mvnw.cmd spring-boot:run
    ```

2.  **Using your IDE:**
    *   Import the project as a Maven project.
    *   Locate the `MicroProjectApplication.java` file.
    *   Right-click and select "Run" or "Debug".

The application will start, and the API will typically be available at `http://localhost:8080`. The console will show logs, including Hibernate SQL statements (`spring.jpa.show-sql=true`).

A default user `testuser` is created on startup if it doesn't exist.

## API Endpoints

Here are the main API endpoints provided:

**Products (`/api/products`)**

*   `GET /`: Get all products.
*   `GET /{id}`: Get a specific product by ID.
*   `POST /`: Create a new product.
*   `PUT /{id}`: Update an existing product.
*   `DELETE /{id}`: Delete a product.
*   `GET /search?name={name}`: Search products by name.
*   `GET /available`: Get all products (seems redundant with `GET /`).

**Users (`/api/users`)**

*   `POST /`: Create a new user (requires `username` and `email` in the request body).

**Cart (`/api/users/{username}/cart`)**

*   `GET /`: Get the cart for the specified user.
*   `POST /items`: Add an item to the user's cart (requires `productId` and `quantity` in the body).
*   `PUT /items/{productId}?quantity={quantity}`: Update the quantity of an item in the cart.
*   `DELETE /items/{productId}`: Remove an item from the cart.
*   `DELETE /`: Clear all items from the user's cart.

**Orders (`/api/users/{username}/orders`)**

*   `POST /`: Place a new order for the user (requires a list of `CartItemDto` in the body).
*   `GET /`: Get all orders for the specified user.

**Favorites (`/api/users/{username}/favorites`)**

*   `GET /`: Get the list of favorite products for the user.
*   `POST /{productId}`: Add a product to the user's favorites.
*   `DELETE /{productId}`: Remove a product from the user's favorites.

**Note:** Security/Authentication is not implemented in the provided controllers. In a real application, you would need to add Spring Security to protect these endpoints and ensure users can only access/modify their own data.

## Configuration

Key configuration options are located in `src/main/resources/application.properties`:

*   **Database Connection:** `spring.datasource.*` properties.
*   **JPA/Hibernate:** `spring.jpa.*` properties.
*   **Server Port:** `server.port`.
*   **Email Settings:** `spring.mail.*` properties (crucial for order confirmations).

---
