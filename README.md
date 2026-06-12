# RuangKelas

RuangKelas is a Spring Boot MVC application built with Spring Boot, Thymeleaf, Spring Security, MySQL, Tailwind CSS, and DaisyUI.

## Prerequisites

Install the following software:

- Java 21 or later
- MySQL 8 or later
- Git

This project includes:

- Maven Wrapper (`mvnw`)
- Automatic Node.js installation via `frontend-maven-plugin`

Therefore, global installations of Maven and Node.js are not required.

Verify Java installation:

```bash
java -version
```

## Clone Repository

```bash
git clone <repository-url>
cd ruangkelas
```

## Recommended Editor Extensions

For Visual Studio Code:

- Extension Pack for Java
- Spring Boot Extension Pack
- Tailwind CSS IntelliSense
- Lombok Annotations Support

## Database Setup

Create the application database:

```sql
CREATE DATABASE `db-ruangkelas`;
```

---

## Environment Variables

Create a `.env` file in the project root:

```env
DB_URL="jdbc:mysql://localhost:3306/db-ruangkelas"
DB_USERNAME=root
DB_PASSWORD=root
```

## Development

### Run Tailwind CSS Watcher

From the frontend directory:

```bash
cd src/main/frontend
npm install
npm run watch
```

This continuously rebuilds:

```text
src/main/resources/static/css/main.css
```

### Run Spring Boot

From the project root:

```bash
./mvnw spring-boot:run
```

Application URL:

```text
http://localhost:8080
```

## Production Mode

Build the application:

```bash
./mvnw clean package
```

The build process will:

1. Download Maven if necessary.
2. Download Node.js if necessary.
3. Install frontend dependencies.
4. Build Tailwind CSS assets.
5. Package the Spring Boot application.

Generated artifact:

```text
target/ruangkelas-0.0.1-SNAPSHOT.jar
```

Run the application:

```bash
java -jar target/ruangkelas-0.0.1-SNAPSHOT.jar
```

