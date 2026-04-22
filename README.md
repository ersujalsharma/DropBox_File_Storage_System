# DropBox File Storage System

A cloud-based file storage system focused on reliable uploads, secure sharing, and scalable synchronization across devices.

## Project Scope

### Phase 1 — MVP
- Upload and download files
- Store file metadata
- Share files via link-based access

### Phase 2 — Scale
- Device sync
- File versioning
- Deduplication
- Notifications

### Phase 3 — Advanced
- Real-time collaboration
- Search (Elastic-style indexing)
- Activity logs and audit trail

## Service Architecture

Split responsibilities into focused backend services instead of one large file service.

1. **File Metadata Service**
   - Stores: `fileId`, `userId`, `path`, `size`, `version`, `checksum`
   - Suggested stack: Java + Spring Boot
   - Suggested database: MongoDB or DynamoDB

2. **Upload Service**
   - Creates upload sessions
   - Generates chunk-level pre-signed URLs
   - Verifies completion and finalizes object assembly

3. **Download Service**
   - Validates authorization
   - Returns short-lived signed URLs
   - Does **not** proxy file bytes through backend services

4. **Sharing Service**
   - Maintains file-to-user access mappings
   - Issues tokenized sharing links with expiry and permissions

5. **Sync Service**
   - Tracks file changes and per-device state
   - Pushes updates via WebSockets or supports polling
   - Consumes file events for near real-time synchronization

## Data Model

### File Metadata
- `file_id` (PK)
- `user_id`
- `file_name`
- `path`
- `size`
- `checksum`
- `version`
- `created_at`

### Chunks
- `chunk_id`
- `file_id`
- `chunk_index`
- `etag`
- `status`

### Sharing
- `share_id`
- `file_id`
- `owner_id`
- `shared_with_user_id`
- `permission` (`read` / `write`)
- `token`
- `expiry`

## Upload Flow

1. Client calls `POST /upload/init`
2. Server computes chunk plan and returns pre-signed part URLs
3. Client uploads chunks in parallel directly to object storage (S3/MinIO)
4. Client tracks part ETags and retries failed chunks
5. Client calls `POST /upload/complete`
6. Server verifies all parts, finalizes multipart upload, and writes metadata
7. Server publishes `FILE_UPLOADED` event

## Optimizations

- **Deduplication**: compute SHA-256; if object exists, only create a new metadata mapping
- **Compression**: optional client-side compression before upload
- **CDN strategy**: cache frequently accessed files with signed URLs + short TTL
- **Path strategy**: `/userId/root/folder1/file.txt`

## Security

- Short-lived pre-signed URLs
- RBAC roles: owner / editor / viewer
- Tokenized sharing links with expiry
- Per-user rate limiting
- Server-side authorization on metadata/share actions

## Event-Driven Backbone

Use Kafka topics for critical domain events:
- `FILE_UPLOADED`
- `FILE_SHARED`
- `FILE_DELETED`

Example consumers:
- Notification service
- Sync service
- Analytics and audit pipeline

## Modules

### Backend
- `auth-service`
- `file-service`
- `upload-service`
- `share-service`

### Infrastructure
- S3 (or MinIO for local development)
- Redis (upload-session cache, idempotency, rate limiting)
- Kafka (event transport)

## Future Enhancements

- File version history
- Soft delete + restore from trash
- Scheduled trash cleanup job
- Search by filename/content
- Activity timeline per file/user

## Documentation Checklist

- Architecture diagram
- API collection (Postman/Bruno)
- Load-test report (file size, concurrency, throughput)
- Retry strategy and failure-mode documentation
- Metrics summary (upload success rate, p95 upload completion latency)

## Overview

This architecture goes beyond basic CRUD storage by emphasizing synchronization, deduplication, and event-driven design for a production-ready system.

## Current Implementation Status

This repository now includes a Spring Boot starter implementation for MVP APIs:
- `POST /api/upload/init`
- `POST /api/upload/complete`
- `GET /api/files?userId=<userId>`
- `GET /api/files/{fileId}/download`
- `POST /api/shares`

> Note: current storage is in-memory for development. S3/MinIO, Redis, and Kafka integrations are planned next.

## Local Run

```bash
mvn spring-boot:run
```

DevTools is enabled for local development, so code changes automatically trigger application restart.

## Basic Frontend (JSP)

A simple JSP UI is available for quick manual testing of APIs:
- URL: `http://localhost:8080/`
- File: `src/main/webapp/WEB-INF/jsp/index.jsp`

The page supports:
- One-click end-to-end file upload (select file → send size/chunk preference to backend → receive signed URLs + chunk size → chunk in browser using server chunk size → upload each chunk → complete)
- Upload init (manual)
- Upload complete (manual)
- Download URL generation
- Share link creation

## API Documentation (Swagger)

After starting the application, open:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## AWS S3 Configuration (for real signed URLs)

Set environment variables before running the app:

```bash
export AWS_ACCESS_KEY_ID=your_access_key
export AWS_SECRET_ACCESS_KEY=your_secret_key
export AWS_REGION=us-east-1
export AWS_S3_BUCKET=your_bucket_name
```

`application.properties` also maps these keys:
- `aws.credentials.access-key=${AWS_ACCESS_KEY_ID:}`
- `aws.credentials.secret-key=${AWS_SECRET_ACCESS_KEY:}`

Optional URL TTL settings:

```bash
export AWS_UPLOAD_URL_TTL_SECONDS=900
export AWS_DOWNLOAD_URL_TTL_SECONDS=300
```

> Security: never commit AWS keys into source code or `application.properties`.

If AWS dependencies fail to resolve, verify Maven can access Maven Central and that the project is using the AWS SDK BOM-managed version from `pom.xml`.

## Run Tests

```bash
mvn test
```
