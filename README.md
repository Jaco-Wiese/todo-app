# Todo App

A simple Todo application with a Spring Boot backend and a Vue.js frontend.

## Local Setup

### Prerequisites

- Docker and Docker Compose installed on your machine

### Running Locally

To start the application locally:

```bash
docker compose up
```

This will:
- Build and start the backend service (Spring Boot) on port 8080
- Build and start the frontend service (Vue.js) on port 3000
- Set up the necessary volumes for data persistence

You can access the application UI at:
```
http://localhost:3000
```

To stop the application:

```bash
docker compose down
```

## Connecting to the Deployed Version

The application is deployed on a VM instance. To connect to it, you need to set up SSH port forwarding.

### Prerequisites

- SSH client installed on your machine

### Connection Steps

1. **Set up port forwarding for the frontend**:

```bash
ssh -L 3000:localhost:3000 username@ip -p port
```

2. **Set up port forwarding for the backend**:

```bash
ssh -L 8080:localhost:8080 username@ip -p port
```

3. **Access the application**:

After setting up the port forwarding, you can access the application at:
```
http://localhost:3000
```

Note: You need to keep the SSH sessions open while using the application.

## Project Structure

- `backend/`: Spring Boot application with REST API
- `frontend/`: Vue.js application for the user interface
- `docker-compose.yml`: Docker Compose configuration for local development

## Additional Information

For information about the CI/CD setup and deployment process, please refer to the [CI-CD-SETUP.md](CI-CD-SETUP.md) file.