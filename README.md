# 🛍️ StyleMart - E-commerce Fashion Platform

https://github.com/user-attachments/assets/eba961d2-b4d0-455a-8565-a7418a85a90b



A modern, full-stack e-commerce platform built with Spring Boot for fashion retail. StyleMart provides a complete online shopping experience with user management, product catalog, shopping cart, order processing, and  dashboard.

## 📋 Table of Contents

- [Features](#-features)
- [Technologies Used](#-technologies-used)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Database Setup](#-database-setup)
- [Configuration](#-configuration)
- [API Endpoints](#-api-endpoints)
- [Security Features](#-security-features)
- [Contributing](#-contributing)
- [License](#-license)

## ✨ Features

### 🛒 E-commerce Features
- **Product Catalog** - Browse products by categories (Men, Women, Kids)
- **Shopping Cart** - Add/remove items with quantity management
- **Order Management** - Complete order processing and tracking
- **User Accounts** - Registration, login, and profile management
- **Address Management** - Multiple delivery addresses
- **Email Notifications** - Order confirmations and account verification

### 👨‍💼 Admin Features
- **Product Management** - CRUD operations for products
- **Order Management** - View and manage customer orders
- **User Management** - Monitor user accounts
- **Inventory Control** - Stock management
- **Sales Analytics** - Order tracking and reporting

### 🔐 Security Features
- **JWT Authentication** - Secure token-based authentication
- **Password Encryption** - BCrypt password hashing
- **Email Verification** - Account verification system
- **CSRF Protection** - Cross-site request forgery protection
- **Role-based Access** - Admin and user role management

## 🛠️ Technologies Used

### Backend
- **Java 17** - Core programming language
- **Spring Boot 3.3.0** - Main framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database operations
- **Spring Mail** - Email functionality
- **Thymeleaf** - Server-side templating
- **PostgreSQL** - Database
- **Lombok** - Reduces boilerplate code
- **JWT** - JSON Web Tokens for authentication

### Frontend
- **HTML5** - Structure and semantics
- **CSS3** - Modern styling with responsive design
- **JavaScript** - Client-side functionality
- **Font Awesome** - Icon library
- **Google Fonts (Inter)** - Typography

## 📁 Project Structure

```
src/main/java/com/example/demo/
├── Controller/          # REST APIs & Web Controllers
│   ├── HomeController.java
│   ├── ProductController.java
│   ├── CartController.java
│   ├── OrderController.java
│   ├── RegistrationController.java
│   ├── AdminController.java
│   ├── AddressController.java
│   ├── VerificationController.java
│   └── ContentController.java
├── Model/              # Database Entities
│   ├── MyAppUser.java
│   ├── Product.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── CartItem.java
│   ├── Address.java
│   └── Repositories
├── Security/           # Authentication & Authorization
│   └── SecurityConfig.java
├── service/            # Business Logic
│   ├── DataInitializationService.java
│   └── EmailService.java
└── utils/              # Utility Classes
    └── JwtTokenUtil.java
```

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- SMTP email service (Gmail recommended)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd project
   ```

2. **Set up PostgreSQL Database**
   ```sql
   CREATE DATABASE registration;
   CREATE USER postgres WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE registration TO postgres;
   ```

3. **Configure application.yml**
   Update the database and email configuration in `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/registration
       username: postgres
       password: your_password
     mail:
       host: smtp.gmail.com
       port: 587
       username: your_email@gmail.com
       password: your_app_password
   ```

4. **Build and run the application**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Access the application**
   - Main application: http://localhost:8080
   - Admin panel: http://localhost:8080/admin

## 🗄️ Database Setup

The application uses PostgreSQL with the following main entities:

### Core Entities
- **MyAppUser** - User accounts and authentication
- **Product** - Product catalog with categories
- **Order** - Customer orders and tracking
- **OrderItem** - Individual items in orders
- **CartItem** - Shopping cart functionality
- **Address** - Delivery addresses

### Database Configuration
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    show-sql: true
```

## ⚙️ Configuration

### Application Properties
Key configuration options in `application.yml`:

```yaml
server:
  error:
    include-message: always
    include-binding-errors: always

spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your_email@gmail.com
    password: your_app_password
    protocol: smtp
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
  datasource:
    url: jdbc:postgresql://localhost:5432/registration
    username: postgres
    password: your_password
```

## 🔌 API Endpoints

### Product Management
- `GET /products` - Get all products
- `GET /products/{id}` - Get product by ID
- `GET /products/category/{category}` - Get products by category
- `POST /products` - Add new product (Admin only)
- `PUT /products/{id}` - Update product (Admin only)
- `DELETE /products/{id}` - Delete product (Admin only)

### Cart Management
- `GET /cart` - Get user's cart
- `POST /cart/add` - Add item to cart
- `PUT /cart/update` - Update cart item quantity
- `DELETE /cart/remove/{id}` - Remove item from cart

### Order Management
- `GET /orders` - Get user's orders
- `POST /orders` - Place new order
- `GET /orders/{id}` - Get order details
- `PUT /orders/{id}/status` - Update order status (Admin only)

### User Management
- `POST /register` - User registration
- `POST /login` - User login
- `POST /verify-email` - Email verification
- `POST /reset-password` - Password reset

### Address Management
- `GET /addresses` - Get user addresses
- `POST /addresses` - Add new address
- `PUT /addresses/{id}` - Update address
- `DELETE /addresses/{id}` - Delete address

## 🔐 Security Features

### Authentication
- **JWT Token-based** authentication
- **BCrypt** password encryption
- **Email verification** for new accounts
- **Password reset** functionality

### Authorization
- **Role-based access control** (User/Admin)
- **CSRF protection** enabled
- **Secure session management**

### Security Configuration
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // BCrypt password encoder
    // JWT token handling
    // Authentication provider
    // Security filter chain
}
```

## 🎨 UI Features

### Design System
- **Responsive design** for all devices
- **Modern UI** with gradient colors
- **Smooth animations** and transitions
- **Category-based** product browsing
- **Shopping cart** functionality
- **Checkout process**

### Key Pages
- **Homepage** (`/`) - Landing page with hero section
- **Products** (`/products`) - Product catalog
- **Cart** (`/cart`) - Shopping cart
- **Checkout** (`/checkout`) - Order checkout
- **Login** (`/login`) - User authentication
- **Signup** (`/signup`) - User registration
- **Orders** (`/orders`) - Order history
- **Admin** (`/admin`) - Admin dashboard

## 📧 Email Integration

### Email Features
- **User registration verification**
- **Password reset emails**
- **Order confirmation emails**
- **Account notifications**

### Email Configuration
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your_email@gmail.com
    password: your_app_password
    protocol: smtp
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

## 🚀 Deployment

### Production Deployment
1. **Build the application**
   ```bash
   mvn clean package -DskipTests
   ```

2. **Run the JAR file**
   ```bash
   java -jar target/demo-0.0.1-SNAPSHOT.jar
   ```

3. **Environment Variables**
   Set the following environment variables for production:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   - `SPRING_MAIL_USERNAME`
   - `SPRING_MAIL_PASSWORD`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the project documentation

---

**StyleMart** - Your Fashion, Your Style! 🛍️✨ 