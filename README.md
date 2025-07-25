# AI-Driven Multi-Vector Threat Intelligence Platform for Bitcoin - Backend

## Overview

This Spring Boot application serves as the backend for the AI-Driven Multi-Vector Threat Intelligence Platform for Bitcoin. It provides comprehensive threat intelligence analysis for Bitcoin addresses through multiple analytical vectors:

1. **Blockchain Analysis** - Examines transaction patterns and network behavior
2. **Threat Intelligence Integration** - Correlates addresses with known malicious activities
3. **AI-Powered Risk Assessment** - Uses machine learning to identify suspicious patterns

## Features

- **User Authentication & Authorization** - Secure JWT-based authentication
- **Transaction Analysis** - Detailed risk assessment of Bitcoin addresses
- **Real-Time Monitoring** - Track Bitcoin network activities and anomalies
- **Alert Management** - Prioritized security alerts with risk levels
- **Report Generation** - Comprehensive PDF reports stored in AWS S3
- **Risk Management** - Tools to evaluate and mitigate cryptocurrency risks
- **API Integration** - Connections to Blockchain.com, Chainabuse.com, and OpenAI

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Oracle 23ai Database
- AWS Account with S3 access
- API keys for external services

## Setup & Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd mvtipbtc-backend
```

### 2. Configure Database

Create a new Oracle user (do not use SYS):

```sql
CREATE USER mvtipbtc IDENTIFIED BY your_password;
GRANT CONNECT, RESOURCE, DBA TO mvtipbtc;
```

Run the database initialization script:

```bash
sqlplus mvtipbtc/your_password@//localhost:1521/XEPDB1 @src/main/resources/database-init.sql
```

### 3. Configure Application Properties

Update `src/main/resources/application.properties` with your specific configuration:

```properties
# Database Configuration
spring.datasource.url=jdbc:oracle:thin:@//localhost:1521/XEPDB1
spring.datasource.username=mvtipbtc
spring.datasource.password=your_password
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.Oracle12cDialect

# JWT Configuration
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000

# AWS Configuration
aws.accessKey=your_aws_access_key
aws.secretKey=your_aws_secret_key
aws.region=us-east-1
aws.s3.bucket=mvtipbtc-reports

# External API Keys
blockchain.api.key=your_blockchain_api_key
chainabuse.api.key=your_chainabuse_api_key
openai.api.key=your_openai_api_key
```

### 4. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

## API Endpoints

### Authentication
- **POST /api/auth/register** - Register a new user
- **POST /api/auth/login** - Authenticate and receive JWT token

### Analysis
- **GET /api/analysis/{address}** - Analyze a Bitcoin address
- **GET /api/analysis/flow/{address}** - Get transaction flow visualization

### Alerts
- **GET /api/alerts** - Get all alerts
- **GET /api/alerts/filter** - Filter alerts by criteria
- **GET /api/alerts/statistics** - Get alert statistics

### Reports
- **GET /api/reports** - Get all reports
- **POST /api/reports/generate** - Generate a new report

### Home
- **GET /api/home/summary** - Get dashboard summary statistics
- **GET /api/home/recent-activity** - Get recent platform activities
- **GET /api/home/risk-categories** - Get risk category distribution
- **GET /api/home/system-status** - Get system operational status

## Testing

Run tests with:

```bash
mvn test
```

Use the included API testing script:

```bash
./enhanced-api-test.sh
```

## Security

- All endpoints (except authentication) require a valid JWT token
- Passwords are encrypted using BCrypt
- Role-based access control is implemented
- Input validation is performed on all endpoints
- CORS is configured for secure frontend-backend communication

## External API Integration

- **Blockchain.com API** - Transaction data and address information
- **Chainabuse.com API** - Scam and fraud reports
- **OpenAI API** - AI-powered analysis and risk assessment

## Troubleshooting

### Common Issues

1. **Database Connection Errors**
   - Verify Oracle service is running
   - Check database credentials in application.properties

2. **API Key Issues**
   - Verify all API keys are valid and not expired
   - Check for rate limiting on external APIs

3. **AWS S3 Access Problems**
   - Verify AWS credentials
   - Check bucket permissions

