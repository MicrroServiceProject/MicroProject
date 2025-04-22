# Integrated Project - Microservices Ecosystem

🌐 **Full-stack application** avec architecture microservices utilisant :
- **Frontend**: Angular  
- **Service Discovery**: Eureka Server  
- **API Gateway**: Spring Cloud Gateway  
- **Microservices**:  
  - User Service  
  - Product Service  
  - Event Service  
  - Courses Service  

## 📂 Structure du Projet

## ⚙ Configuration Principale

### 1. **Eureka Server** (port 8761)
```properties
# application.properties
server.port=8761
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
