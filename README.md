# Deepfake Detection - Spring Boot Backend

This is the backend part of a Deepfake Detection project built with **Spring Boot**.  
It handles user authentication, image uploads, and PostgreSQL-based storage for image metadata.  
This backend is designed to work together with a FastAPI-based AI model service and a frontend interface.

---

##  Main Features

-  User registration & login  
-  Image upload via REST API  
-  PostgreSQL database integration  
-  Retrieve uploaded images by user  
-  CORS enabled for frontend-backend communication

---

## 🛠️ Tech Stack

- Java 17+  
- Spring Boot  
- Spring Security  
- Spring Data JPA  
- PostgreSQL  
- Maven

---
## 🔗 API Endpoints

| Method | Endpoint                | Description                   |
|--------|-------------------------|-------------------------------|
| POST   | `/api/auth/register`    | Registers a new user          |
| POST   | `/api/auth/login`       | Authenticates user credentials and returns a token |
| POST   | `/api/images/upload`    | Uploads an image file         |
| GET    | `/api/images/user/{id}` | Retrieves all images uploaded by a specific user |


## 📦 How to Run

1. **Clone the project:**

   ```bash
   git clone https://github.com/busraozcn/DeepfakeDetection-Spring-Backend/.git
   cd deepfake-spring-backend 
   ```
2. **Configure the database in src/main/resources/application.properties:**
3. **Run the application**

   
   
