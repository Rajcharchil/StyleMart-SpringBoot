# 🛍️ StyleMart - E-commerce Fashion Platform

## 📋 Project Overview
यह एक **Spring Boot** आधारित **E-commerce fashion platform** है जो **StyleMart** नाम से है। यह एक online clothing store है जहाँ users fashion items खरीद सकते हैं।

---

## 🛠️ Technologies Used

### Backend Technologies:
- **Java 17** - Main programming language
- **Spring Boot 3.3.0** - Main framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Database operations
- **Spring Mail** - Email functionality
- **Thymeleaf** - Server-side templating
- **PostgreSQL** - Database
- **Lombok** - Reduces boilerplate code
- **JWT (JSON Web Tokens)** - Token-based authentication

### Frontend Technologies:
- **HTML5** - Structure
- **CSS3** - Styling with modern design
- **JavaScript** - Client-side functionality
- **Font Awesome** - Icons
- **Google Fonts (Inter)** - Typography

---

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
└── utils/              # Utility Classes
```

---

## 🎯 Main Features

### 1. User Management:
- User registration & login
- Email verification system
- Password reset functionality
- User profiles management

### 2. Product Management:
- Product catalog with categories (Men, Women, Kids)
- Product search & filtering
- Product images & descriptions
- Stock management
- Product ratings & reviews

### 3. Shopping Cart:
- Add/remove items functionality
- Quantity management
- Cart persistence across sessions

### 4. Order Management:
- Order placement process
- Order tracking system
- Order history for users
- Address management

### 5. Admin Features:
- Product management (CRUD operations)
- Order management
- User management
- Inventory control

---

## 🗄️ Database Schema

### Main Entities:

#### MyAppUser (User Accounts)
```java
- id (Primary Key)
- username
- email
- password (encrypted)
- verificationToken
- isVerified
- resetToken
```

#### Product (Product Catalog)
```java
- id (Primary Key)
- name
- price
- stock
- category
- images (List)
- description
- rating
- reviewCount
- active
```

#### Order (Customer Orders)
```java
- id (Primary Key)
- user (ManyToOne)
- orderItems (OneToMany)
- totalAmount
- status
- orderDate
- shippingAddress
```

#### OrderItem (Order Details)
```java
- id (Primary Key)
- order (ManyToOne)
- product (ManyToOne)
- quantity
- price
```

#### CartItem (Shopping Cart)
```java
- id (Primary Key)
- user (ManyToOne)
- product (ManyToOne)
- quantity
```

#### Address (Delivery Addresses)
```java
- id (Primary Key)
- user (ManyToOne)
- street
- city
- state
- zipCode
- isDefault
```

---

## 🔐 Security Features

### Authentication & Authorization:
- **BCrypt** password encryption
- **JWT** token authentication
- **Spring Security** configuration
- **CSRF** protection
- **Role-based** access control

### Security Configuration:
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

---

## 📧 Email Integration

### Email Configuration:
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: email@gmail.com
    password: password
    protocol: smtp
```

### Email Features:
- User registration verification
- Password reset emails
- Order confirmation emails
- Account notifications

---

## 🎨 UI/UX Features

### Design System:
- **Responsive design** for mobile & desktop
- **Modern UI** with gradient colors
- **Smooth animations** & transitions
- **Category-based** product browsing
- **Shopping cart** functionality
- **Checkout process**

### Color Scheme:
```css
:root {
  --clr-primary: #ff3f6c;
  --clr-dark: #212529;
  --clr-light: #fff;
  --clr-muted: #6c757d;
  --clr-bg: #f7f7f7;
}
```

### Key Pages:
- **index.html** - Homepage with hero section
- **products.html** - Product catalog
- **cart.html** - Shopping cart
- **checkout.html** - Order checkout
- **login.html** - User authentication
- **signup.html** - User registration
- **orders.html** - Order history
- **female.html** - Women's category
- **kids.html** - Kids' category

---

## 🚀 Key Controllers

### HomeController
```java
@Controller
public class HomeController {
    @GetMapping("/") - Homepage
    @GetMapping("/female") - Women's section
    @GetMapping("/kids") - Kids' section
}
```

### ProductController
```java
@RestController
@RequestMapping("/products")
public class ProductController {
    // Get all products
    // Get product by ID
    // Get products by category
    // Search products
}
```

### CartController
```java
@RestController
@RequestMapping("/cart")
public class CartController {
    // Add to cart
    // Remove from cart
    // Update quantity
    // Get cart items
}
```

### OrderController
```java
@RestController
@RequestMapping("/orders")
public class OrderController {
    // Place order
    // Get order history
    // Order tracking
    // Order management
}
```

### RegistrationController
```java
@Controller
public class RegistrationController {
    // User registration
    // Email verification
    // Password reset
}
```

### AdminController
```java
@Controller
public class AdminController {
    // Admin dashboard
    // Product management
    // Order management
    // User management
}
```

---

## 💾 Database Configuration

### PostgreSQL Setup:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/registration
    username: postgres
    password: Sumangal@16
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

### Database Features:
- **Auto table creation** (ddl-auto: update)
- **Hibernate ORM** for database operations
- **Connection pooling** for performance
- **SQL logging** for debugging

---

## 🔄 Business Logic Workflow

### 1. User Journey:
```
Registration → Email Verification → Login → Browse Products → Add to Cart → Checkout → Order Confirmation
```

### 2. Product Management:
```
Admin Login → Add/Edit Products → Manage Inventory → Update Categories → Monitor Sales
```

### 3. Order Processing:
```
Order Placement → Payment Processing → Inventory Update → Email Notification → Order Tracking
```

---

## 🎯 Key Highlights

### Technical Excellence:
1. **Modern Spring Boot** architecture
2. **Secure authentication** with JWT
3. **Responsive web design**
4. **Complete e-commerce** functionality
5. **Email integration** for notifications
6. **PostgreSQL** for reliable data storage
7. **Thymeleaf** for server-side rendering

### Business Features:
1. **Multi-category** product catalog
2. **Advanced search** and filtering
3. **Shopping cart** functionality
4. **Order management** system
5. **User account** management
6. **Admin dashboard** for business operations
7. **Email notifications** for user engagement

### Security Features:
1. **Password encryption** with BCrypt
2. **JWT token** authentication
3. **CSRF protection**
4. **Role-based** access control
5. **Email verification** for account security

---

## 🚀 Deployment & Configuration

### Prerequisites:
- Java 17
- PostgreSQL Database
- Maven for build management
- SMTP email configuration

### Configuration Files:
- **application.yml** - Main configuration
- **pom.xml** - Maven dependencies
- **SecurityConfig.java** - Security settings

### Build & Run:
```bash
mvn clean install
mvn spring-boot:run
```

---

## 📊 Project Summary

यह project एक **complete e-commerce solution** है जो real-world business requirements को handle करता है और modern web development practices का use करता है।

### What Makes It Special:
- **Full-stack** e-commerce solution
- **Modern architecture** with Spring Boot
- **Secure** authentication system
- **Responsive** design for all devices
- **Complete** business workflow
- **Professional** code structure
- **Scalable** database design

### Target Users:
- **Customers** - Browse and purchase fashion items
- **Admins** - Manage products, orders, and users
- **Business Owners** - Monitor sales and inventory

यह एक **production-ready** e-commerce platform है जो real business needs को fulfill करता है। 