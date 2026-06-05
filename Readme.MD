# Multi-Tenant Notification Service

This project is a multi-tenant notification service designed to send notifications via email, SMS, and push notifications. It's built with Spring Boot and provides a RESTful API for managing tenants, notification templates, and sending notifications.

## Key Features

*   **Multi-tenancy:** The service is designed to support multiple tenants, with each tenant having its own set of configurations and resources.
*   **Multiple Notification Channels:** Supports sending notifications via email, SMS, and push notifications.
*   **Rate Limiting:** Implements rate limiting to control the number of notifications a tenant can send in a given time period.
*   **Asynchronous Notification Dispatch:** Notifications are sent asynchronously to avoid blocking the main application thread.
*   **Retry Logic:** Includes a retry mechanism for failed notification deliveries.

## Technologies Used

*   **Java 17**
*   **Spring Boot:** The core framework for building the application.
*   **Spring Data JPA:** For data access and persistence.
*   **PostgreSQL:** The database used to store application data.
*   **Maven:** For dependency management and building the project.

## Getting Started

### Prerequisites

*   Java 17 or later
*   Maven 3.2+
*   PostgreSQL

### Configuration

1.  **Database Setup:**
    *   Create a PostgreSQL database named `postgres`.
    *   Create a schema named `tenant_notification`.
    *   Update the `spring.datasource.username` and `spring.datasource.password` in `src/main/resources/application.yaml` with your PostgreSQL credentials.

2.  **Build and Run:**
    *   Clone the repository:
        ```bash
        git clone <repository-url>
        ```
    *   Navigate to the project directory:
        ```bash
        cd multi-tenant-notifcation-ankit-kathait
        ```
    *   Build the project using Maven:
        ```bash
        mvn clean install
        ```
    *   Run the application:
        ```bash
        mvn spring-boot:run
        ```

The application will be available at `http://localhost:8080`.

## Project Architecture

The project follows a standard layered architecture:

*   **`config`:** Contains Spring configuration classes.
*   **`dto`:** Data Transfer Objects for API requests and responses.
*   **`entity`:** JPA entities representing the database tables.
*   **`enums`:** Enumerations used throughout the application.
*   **`exception`:** Custom exception classes.
*   **`interceptor`:** Interceptors for handling cross-cutting concerns like rate limiting.
*   **`provider`:** Concrete implementations for different notification channels.
*   **`repository`:** Spring Data JPA repositories for database access.
*   **`resource`:** REST controllers that expose the API endpoints.
*   **`service`:** Business logic for the application.

## Assumptions for Interview Scope

Due to the time constraints of an interview setting, the following assumptions were made:

*   **Simplified Queuing Model:** The project does not use a dedicated message broker (like RabbitMQ or Kafka) for asynchronous processing. Instead, it uses Spring's built-in `@Async` functionality with a `ThreadPoolTaskExecutor`. In a large-scale production environment, a more advanced, multi-queue system would be implemented to ensure per-tenant fairness and differentiated service. The ideal architecture would be:
    1.  **Priority Tiers:** Establish multiple types of queues, such as `high`, `medium`, and `low` priority. Tenants could be assigned to a tier based on their service level agreement (SLA).
    2.  **Weighted Polling Consumers:** The worker services (consumers) would poll for messages using a weighted algorithm (e.g., 60% of polls for `high`, 30% for `medium`, 10% for `low`). This ensures high-priority queues are serviced more frequently while preventing the starvation of lower-priority queues.
*   **Simple Retry Logic:** The retry logic is implemented using Spring Retry. For a more robust solution, a more sophisticated retry mechanism with exponential backoff and dead-letter queues would be necessary.
*   **No JWT/Authentication:** The API endpoints are not secured with JWT or any other authentication mechanism. For simplicity, tenant, user, and role information is passed via request headers. In a real-world application, this information would be part of the JWT claims, and securing the API would be a top priority. This was omitted to focus on the core notification functionality.
*   **Basic Reporting:** The project includes a basic, paginated reporting endpoint for tenants to view their delivery history. This is a crude form of audit reporting that queries the transactional database directly. In a production system, a more robust solution would be implemented by pushing all delivery status events to a data lake (e.g., Hadoop/S3). A data visualization platform (e.g., Grafana, Kibana) would then be used to build powerful, scalable dashboards and reports on top of this data, avoiding direct queries to the operational database.