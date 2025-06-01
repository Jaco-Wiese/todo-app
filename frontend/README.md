# Todo App Frontend

This is the frontend for the Todo application built with Nuxt.js.

## Docker Setup

This project includes Docker configuration for easy deployment and development.

### Prerequisites

- Docker
- Docker Compose

### Building and Running with Docker

1. Build and start the frontend container:

```bash
docker-compose up -d
```

2. Access the application at http://localhost:3000

### Environment Variables

The following environment variables can be configured:

- `NUXT_PUBLIC_API_BASE_URL`: URL of the backend API (default: http://localhost:8080)

You can modify these in the `docker-compose.yml` file or pass them directly when running Docker.

### Building for Production

To build the Docker image for production:

```bash
docker build -t todo-app-frontend .
```

### Running Without Docker Compose

If you prefer to run the container without Docker Compose:

```bash
docker run -p 3000:3000 -e NUXT_PUBLIC_API_BASE_URL=http://your-api-url:8080 todo-app-frontend
```

## Development Without Docker

If you prefer to develop without Docker:

1. Install dependencies:

```bash
npm install
```

2. Start the development server:

```bash
npm run dev
```

3. Access the application at http://localhost:3000
