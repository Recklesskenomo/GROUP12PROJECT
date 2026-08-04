# Courier & Logistics System

A 3-tier Java RMI distributed application for courier and logistics management.

## Architecture

| Tier | Module | Description |
|------|--------|-------------|
| **Common** | `courier-common` | Shared DTOs and RMI Remote Interface |
| **Server** | `courier-server` | RMI Server + JDBC DAO (MySQL via HeidiSQL) |
| **Client** | `courier-client` | JavaFX UI for Admin (Dispatcher) and Sender (User) |

## Prerequisites

- **Java 17+** (JDK)
- **Maven 3.8+**
- **MySQL/MariaDB** (managed via HeidiSQL)

## Building

```bash
# Clone the repository
git clone https://github.com/Recklesskenomo/GROUP12PROJECT.git
cd GROUP12PROJECT

# Build all modules
mvn clean install
```

## Running

### 1. Start the Database
- Open HeidiSQL and ensure your MySQL server is running.
- Import the schema from `courier-server/src/main/resources/schema.sql` (coming soon).

### 2. Start the RMI Server
```bash
cd ~/Documents/GROUP12PROJECT/courier-server
# Pass your hotspot IP as the first argument
mvn exec:java -Dexec.args="192.168.x.x"
```

### 3. Start a Client
```bash
cd ~/Documents/GROUP12PROJECT/courier-client
# Pass the server's hotspot IP as the first argument
mvn org.openjfx:javafx-maven-plugin:run -Dexec.args="192.168.x.x"
# OR:
mvn exec:java -Dexec.args="192.168.x.x"
```

## Hotspot Testing

Since we test over mobile hotspot, IPs change frequently:
1. The host runs `ipconfig` (Windows) or `ip addr` (Linux) to find their hotspot IP.
2. Pass that IP when starting the server.
3. Share that IP with teammates — they pass it when starting clients.

## Team

Group 12 — University Assignment (Due: August 10, 2026)

## Module Structure

```
GROUP12PROJECT/
├── pom.xml                        ← Parent POM
├── courier-common/                ← Shared DTOs + RMI Interface
├── courier-server/                ← RMI Server + JDBC DAO
└── courier-client/                ← JavaFX Clients
```
