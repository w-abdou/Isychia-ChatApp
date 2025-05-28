
# Isychia-ChatApp

**Isychia-ChatApp** is a Java-based, end-to-end encrypted chat application that enables secure real-time messaging between users. Built using Java and JavaFX for the user interface, and powered by file-based persistence (or optionally a database), this project is designed to provide a clean, minimal, yet secure peer-to-peer communication experience.

##  Key Features

*  **End-to-End Encryption (E2EE)**
  All messages are encrypted on the sender's device and can only be decrypted by the intended recipient. Even the server cannot read the messages.

*  **User Registration & Authentication**
  Lightweight login and registration system with basic credentials stored securely.

*  **Real-Time Messaging**
  Send and receive encrypted messages instantly through socket-based communication.

*  **Data Persistence**
  Uses file-based storage (`users.dat`) to persist user credentials and optionally chats.

*  **JavaFX GUI**
  Built-in GUI using JavaFX for a responsive and modern desktop chat client.

*  **Scalable Codebase**
  Modular design following best practices for Java applications, supporting future upgrades like group chat, file sharing, and database integration.

---

##  Architecture Overview

* **Frontend:** JavaFX desktop GUI
* **Backend:** Java with multithreaded server handling socket communication
* **Encryption:** Custom E2EE using Java cryptography libraries (e.g., AES, RSA)
* **Storage:** Serialized user data via `users.dat`

---

##  Project Structure

```
Isychia-ChatApp/
├── .idea/                 # IntelliJ IDEA configs
├── .mvn/                  # Maven wrapper settings
├── src/
│   └── main/
│       ├── java/
│       │   └── isychia/   # Main Java application package
│       │       ├── client/      # JavaFX client UI
│       │       ├── server/      # Server-side socket logic
│       │       ├── encryption/  # E2EE utility classes
│       │       └── models/      # Data models (User, Message)
│       └── resources/     # Application configuration and assets
├── users.dat              # Serialized user data (basic persistence)
├── mvnw / mvnw.cmd        # Maven wrapper scripts
├── pom.xml                # Project dependencies and build config
└── README.md              # This file
```

---

##  Getting Started

### Prerequisites

* Java 17+ (Java 21 recommended)
* Maven 3.8+
* JavaFX SDK (for running UI-based modules)
* IntelliJ IDEA (recommended) or any IDE

### 1. Clone the repository

```bash
git clone https://github.com/w-abdou/Isychia-ChatApp.git
cd Isychia-ChatApp
```

### 2. Build and Run the Project

To build and run the chat application using Maven:

```bash
./mvnw clean install
./mvnw javafx:run
```

Or run via your IDE (ensure JavaFX is properly configured).

---

##  How E2EE Works

1. When users start a session, each user has a public/private key pair.
2. Messages are encrypted with the recipient’s public key and can only be decrypted using their private key.
3. This ensures that only the intended recipient can view the message contents.

➡ Future plans include adding key rotation, stronger handshake protocols, and optional post-quantum cryptography support.

