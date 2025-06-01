# 🌱 Terra Nostra

**Terra Nostra** es una plataforma web de e-commerce centrada en productos naturales y sostenibles. El sistema está dividido en dos subproyectos:

- **API REST (Spring Boot)**: Gestión de datos, seguridad, lógica de negocio.
- **Web Dinámico (JSP + Tomcat)**: Interfaz de usuario, gestión del carrito, vistas JSP.

---

## 📁 Estructura del Proyecto

terra-nostra/
-├── api/ # API REST con Spring Boot
-│ ├── Dockerfile
-│ ├── pom.xml
-│ └── src/main/java/...
-├── web/ # Aplicación web con JSP
-│ ├── Dockerfile
-│ ├── pom.xml
-│ └── src/main/webapp/
-├── db/ # Scripts y dump de base de datos
-├── docker-compose.yml # Orquestación de contenedores
-└── README.md


---


---

## ⚙️ Tecnologías Usadas

### Backend (API)

- Java 21 + Spring Boot 3  
- Spring Security + JWT  
- JPA (Hibernate) + MySQL 8  
- JavaMail para correos de verificación y facturas  
- Docker  

### Frontend (Web Dinámico)

- JSP + JSTL  
- JavaScript (AJAX, Fetch API)  
- CSS personalizado + FontAwesome
  
---

## 🔐 Seguridad

- Login con verificación de correo electrónico  
- Autenticación y autorización con JWT  
- Contraseñas cifradas con BCrypt  
- Protección de endpoints  

---

## 🛒 Funcionalidades Clave

- Catálogo de productos  
- Carrito persistente en base de datos  
- Reseñas con verificación de sesión  
- Pedidos y facturas PDF  
- Verificación de cuenta por correo  
- Recuperación de contraseña  
- Perfil de usuario con pedidos históricos  
- Integración de pago con PayPal  
- Diseño responsive personalizado  
- Panel de administración  

---

## 🐳 Docker y Despliegue

### Requisitos

- Docker + Docker Compose  
- Dominio y certificados SSL (Let’s Encrypt o similar)

### Comandos

```
# Construcción y ejecución
docker compose up -d --build

# Ver logs
docker compose logs -f

# Detener contenedores
docker compose down


### Servicios expuestos

| Servicio | Puerto | URL                                      |
|----------|--------|-------------------------------------------|
| API      | 8081   | http://localhost:8081/api/...             |
| Web      | 8082   | http://localhost:8082                    |
| MySQL    | 3306   | Localhost                                 |
| NGINX    | 443/80 | https://terra-nostra.tudominio.com       |

## 🧠 Autor

**Eduardo Ballesteros Pérez**  

GitHub: [@eduballesteros](https://github.com/eduballesteros)

