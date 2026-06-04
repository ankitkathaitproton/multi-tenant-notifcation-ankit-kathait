# Multi-Tenant Notification Service: Implementation Plan (Simplified Auth)

This document outlines the core functionalities, database entities, proposed REST API designs, and underlying code flows for the Multi-Tenant Notification Service using Spring Boot.

*Note: Authentication is mocked for this scope. Identity and roles are passed via standard HTTP headers (`X-Tenant-ID`, `X-User-ID`, `X-User-Role`) to simulate an API Gateway passing context to this downstream service.*

---

## 1. Tenant & Security Management

### Core Functionality
* **Tenant Provisioning:** Platform admins can onboard new tenants and manage their lifecycle.
* **Role-Based Access Control (RBAC):** Simple conditional checks in the controller layer using the `X-User-Role` header to separate Platform Admins and Tenant Admins.
* **Security & Validation:** Robust input validation and clean error handling across all layers.

### Proposed API Endpoints (Platform Admin)
* `POST /api/v1/admin/tenants` - Create a new tenant. (Requires `X-User-Role: PLATFORM_ADMIN`)
* `GET /api/v1/admin/tenants/{tenantId}` - Retrieve tenant details.
* `PUT /api/v1/admin/tenants/{tenantId}/status` - Activate or suspend a tenant.

### POJOs / Database Entities
* **`Tenant` (Entity):**
    * `UUID id` (Primary Key)
    * `String name`
    * `String status` (`ACTIVE`, `SUSPENDED`)
    * `Instant createdAt`
    * `Instant updatedAt`

### Application Code Flow
    [Client Request with Headers: X-User-Role=PLATFORM_ADMIN] 
           │
           ▼
    [TenantController] ──────────────► 1. Checks if X-User-Role == PLATFORM_ADMIN (Throws 403 otherwise)
           │                           2. Validates input constraints (e.g., @Valid name non-empty)
           │
           ▼
    [TenantService] ─────────────────► Executes business logic (e.g., checks for duplicate tenant names)
           │
           ▼
    [TenantRepository] ──────────────► Persists the Tenant entity to the Database
           │
           ▼
    [API Response] ──────────────────► Returns HTTP 201 Created with the generated Tenant metadata

---

## 2. Rate Limiting & Fairness

### Core Functionality
* **Global Limit Management:** Platform admins define and manage global throughput limits across the service.
* **Per-Tenant Rate Limits:** Enforce strict, configurable rate limits on a per-tenant basis.
* **Fairness Under Load:** Enforce per-tenant fairness under heavy load so a single noisy tenant cannot starve bounded worker pools.
* **HTTP 429 Handling:** Reject excess API requests immediately at the controller layer.

### Proposed API Endpoints (Platform Admin)
* `PUT /api/v1/admin/tenants/{tenantId}/rate-limits` - Configure specific API and dispatch limits for a tenant.
* `GET /api/v1/admin/global-limits` - View system-wide global thresholds.
* `PUT /api/v1/admin/global-limits` - Update system-wide global thresholds.

### POJOs / Database Entities
* **`TenantRateLimit` (Entity):**
    * `UUID id`
    * `UUID tenantId` (Foreign Key -> Tenant)
    * `Integer requestsPerSecond` (For API rate-limiting)
    * `Integer maxConcurrentJobs` (For concurrency enforcement during worker dispatch)

### Application Code Flow
    [Client Request]
           │
           ▼
    [RateLimitingInterceptor] ───────► Extracts `X-Tenant-ID` header
           │
           ├──► [Cache / In-Memory Bucket4j] ──► Checks remaining token quota for that Tenant ID
           │
           ├──► Quota Exhausted? ──────────────► Throws RateLimitExceededException ──► Returns HTTP 429
           │
           ▼ (Quota Available)
    [Controller] ────────────────────► Proceeds to route the request

---

## 3. Channel & Template Configuration

### Core Functionality
* **Channel Configuration:** Allows tenant admins to set up delivery channels (Email, SMS, Push, In-App).
* **Template CRUD:** Dedicated endpoints for managing reusable templates.
* **Variable Substitution Engine:** Dynamically processes and replaces variable placeholders within templates before dispatch.

### Proposed API Endpoints (Tenant Admin)
*(All endpoints require `X-Tenant-ID` and `X-User-Role: TENANT_ADMIN` headers)*
* `POST /api/v1/channels` - Configure a delivery channel (e.g., SMTP, Twilio keys).
* `GET /api/v1/channels` - List active channels for the tenant.
* `POST /api/v1/templates` - Create a new notification template.
* `PUT /api/v1/templates/{templateId}` - Update an existing template.
* `GET /api/v1/templates` - List all templates for the tenant.

### POJOs / Database Entities
* **`ChannelConfig` (Entity):**
    * `UUID id`
    * `UUID tenantId`
    * `ChannelType` type (`EMAIL`, `SMS`, `PUSH`, `IN_APP`)
    * `String encryptedCredentials`
    * `Boolean isActive`
* **`NotificationTemplate` (Entity):**
    * `UUID id`
    * `UUID tenantId`
    * `String name`
    * `ChannelType` channelType
    * `String content` (e.g., "Hello {{name}}, your OTP is {{otp}}.")

### Application Code Flow (Template Creation)
    [Tenant Admin Request w/ Headers] 
                                      │
                                      ▼
                               [TemplateController] ──► 1. Verifies Role is TENANT_ADMIN
                                      │                 2. Binds X-Tenant-ID to the request context
                                      │                 3. Validates request payload structures
                                      │
                                      ▼
                               [TemplateService] ─────► Parses the content string to verify bracket syntax
                                      │
                                      ▼
                               [TemplateRepository] ──► Saves NotificationTemplate entity to Database mapped to Tenant ID
                                      │
                                      ▼
                               [API Response] ────────► Returns HTTP 201 Created

---

## 4. Core Notification & Dispatch Engine

### Core Functionality
* **Send APIs:** Ingestion endpoints for immediate and scheduled notification requests.
* **Concurrency Handling:** Processes high volumes of messages concurrently via thread isolation and bounded worker pools.
* **Fairness Dispatching:** Ensures worker pools distribute bandwidth fairly among competing active tenants.

### Proposed API Endpoints
*(Requires `X-Tenant-ID` header)*
* `POST /api/v1/notifications/send` - Trigger an immediate notification.
* `POST /api/v1/notifications/schedule` - Schedule a notification for a future timestamp.
* `DELETE /api/v1/notifications/{notificationId}/schedule` - Cancel a scheduled notification.

### POJOs / Database Entities
* **`NotificationRequest` (DTO):**
    * `UUID templateId`
    * `String recipient`
    * `Map<String, String> templateVariables`
    * `String idempotencyKey`
* **`ScheduledNotification` (Entity):**
    * `UUID id`
    * `UUID tenantId`
    * `UUID templateId`
    * `String recipient`
    * `String templateVariablesJson`
    * `Instant scheduledTime`
    * `String status` (`PENDING`, `DISPATCHED`, `CANCELLED`)

### Application Code Flow (Immediate Send with Bounded Worker Pools)
    [Client POST /send w/ Headers] ──► [NotificationController] ──► Validates payload and extracts X-Tenant-ID
                                   │
                                   ▼
                            [DispatchService] ────────► 1. Fetches Template & ChannelConfig matching Tenant ID
                                   │                    2. Calls VariableSubstitutionEngine to render body
                                   │                    3. Generates a unique Notification ID
                                   │
                                   ▼
                        [FairnessQueue/Dispatcher] ───► Evaluates concurrent allocations per tenant.
                                   │                    If capacity allows, grabs a slot.
                                   │
                                   ▼
                         [Bounded Worker Pool] ───────► Hands off processing task to a dedicated thread 
                                   │                    from a custom ThreadPoolExecutor.
                                   │
                                   ▼
                         [Async Task Execution] ─────► Calls downstream network adapter (Email/SMS API)
                                   │
                                   ▼
                        [Client Receives Response] ──► Returns HTTP 202 Accepted immediately with Notification ID

---

## 5. Reliability & Delivery Tracking

### Core Functionality
* **State Machine Persistence:** Reliably logs delivery transitions (`QUEUED`, `PROCESSING`, `SENT`, `FAILED`).
* **Retry & Backoff Mechanism:** Captures transient provider dropouts and automatically schedules retries via exponential backoff.
* **Idempotency Execution:** Protects downstream endpoints from processing duplicate runs for the same business action.
* **Audit Trail / Reporting:** Aggregates delivery data for administrative review and analysis.

### Proposed API Endpoints (Tenant Admin)
*(Requires `X-Tenant-ID` header)*
* `GET /api/v1/notifications/{notificationId}/status` - Check real-time tracking data and state historical changes.
* `GET /api/v1/reports/deliveries` - Fetch paginated and filtered historical reports for the tenant.

### POJOs / Database Entities
* **`NotificationDelivery` (Entity):**
    * `UUID id`
    * `UUID tenantId`
    * `ChannelType` channelType
    * `String recipient`
    * `String renderedContent`
    * `DeliveryStatus` currentStatus (`QUEUED`, `PROCESSING`, `SENT`, `FAILED`)
    * `Integer retryCount`
    * `String idempotencyKey`
* **`DeliveryAuditLog` (Entity):**
    * `UUID id`
    * `UUID notificationDeliveryId` (Foreign Key)
    * `DeliveryStatus` changedToStatus
    * `String remarks` (e.g., Error stack-trace snippet or vendor confirmation code)
    * `Instant timestamp`

### Application Code Flow (Reliable Execution & Backoff Lifecycle)
    [Worker Thread Starts Task]
                │
                ▼
    [Idempotency Checker] ──────────► Queries DB via transaction using X-Tenant-ID + idempotencyKey
                │
                ├──► Already Marked SENT/PROCESSING? ──► Terminate immediately (Prevents duplicate delivery)
                │
                ▼ (Unique Execution)
     [Update Status to PROCESSING] ──► Appends entry to DeliveryAuditLog
                │
                ▼
     [Downstream Third-Party API] ──► Attempts live delivery request (e.g., Sends HTTP call to SMS gateway)
                │
                ├──► [Success] ──────► 1. Updates state to SENT
                │                      2. Appends success event to DeliveryAuditLog
                │
                └──► [Transient Failure Caught]
                            │
                            ▼
                [RetryPolicyManager] ──► 1. Checks if current retryCount < maxAllowedRetries
                            │            2. Computes delay using Exponential Backoff (e.g., base * 2^retryCount)
                            │            3. Schedules delivery retrial task
                            │            4. Updates state to QUEUED & updates logs with error description
                            │
                            ▼
                [Max Retries Exhausted] ──► Sets final state to FAILED & notes terminal status in Audit Logs