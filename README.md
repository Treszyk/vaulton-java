# Vaulton

Vaulton is a zero-knowledge password manager designed around an anonymous AccountId identity model. This repository contains a Java Spring Boot port of the .NET original backend, focused on strict Clean Architecture and 100% frontend compatibility.

## Core Design

- **Anonymous Identity**: No emails or usernames; account identity is purely an opaque AccountId.
- **Triple Verifier System**: Distinct cryptographic proofs for Authentication, Administrative actions, and Account Recovery.
- **Zero-Knowledge**: The server never sees master passwords or keys. All encryption and decryption occur on the client.
- **Hexagonal Architecture**: A Java rewrite emphasizing domain isolation and testability using the Ports and Adapters pattern.

## Repository Contents

- `frontend/`: Original Angular web application.
- `vaulton-api/`: The Java Spring Boot 4.0 backend.

## Development Setup

The project uses Docker Compose to orchestrate the backend, frontend, and PostgreSQL database.

### Quick Start

1. **Prepare Environment**:
   ```bash
   cp .env.dev.example .env
   ```
2. **Build and Run**:
   ```bash
   docker compose -f docker-compose.dev.yml up --build
   ```

The frontend will be available at `http://localhost:4200` and the API at `http://localhost:8080`.

## License

Licensed under the [MIT License](LICENSE).
