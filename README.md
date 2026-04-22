# 🧠 Prescription AI Recommendation System

An end-to-end system that processes medical prescription images and returns relevant exercise video recommendations using AI. Designed with a strong focus on **scalability, security, performance optimization, and user experience**.

---

## 🚀 Overview

This project demonstrates a production-grade architecture where:

* 📱 Android app uploads a prescription image
* ⚙️ Spring Boot backend processes the request
* 🗄️ PostgreSQL is checked first (DB-first caching strategy)
* 🤖 Gemini AI is called only if data is not already available
* 🎯 Exercise recommendations are returned to the user

---

## 🏗️ Architecture

```
Android App (Java + XML)
        │
        ▼
Spring Boot Backend (WebClient - Reactive)
        │
        ├──► PostgreSQL (Cache / DB-first lookup)
        │
        └──► Gemini AI API (External call if needed)
        │
        ▼
Exercise Recommendation Engine
        │
        ▼
Response → Android UI
```

---

## 📌 Key Features

### ✅ 1. Database-First Strategy (Quota Optimization)

* Prevents unnecessary external API calls
* Reduces latency and cost
* Uses image hashing to detect duplicate prescriptions

---

### ✅ 2. Resilient Error Handling

* Handles unclear/blurry prescriptions gracefully
* Returns meaningful messages instead of generic errors

**Example:**

```
"Prescription unclear. Please retake image."
```

---

### ✅ 3. Secure API Key Management

* Gemini API key stored using environment variables
* No secrets exposed in code or repository

---

### ✅ 4. Reactive Backend (WebClient)

* Non-blocking API calls
* Supports high concurrency
* Efficient resource utilization

---

### ✅ 5. Improved User Experience

* Loading indicator during AI processing
* Clear success/error states
* Smooth UI interaction

---

## 🧰 Tech Stack

| Layer       | Technology           |
| ----------- | -------------------- |
| Frontend    | Android (Java, XML)  |
| Backend     | Spring Boot          |
| Database    | PostgreSQL           |
| AI Service  | Gemini API           |
| HTTP Client | WebClient (Reactive) |

---

## 📂 Project Structure

```
root/
│
├── backend/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   ├── config/
│   ├── exception/
│   └── application.yml
│
├── android/
│   ├── activities/
│   ├── network/
│   ├── ui/
│   └── utils/
│
└── README.md
```

---

## 🔄 Request Flow

1. User uploads prescription image from Android app
2. Backend generates image hash
3. Database is checked:

   * ✔ If exists → return cached result
   * ❌ If not → call Gemini AI
4. AI processes prescription
5. Exercise recommendations generated
6. Result stored in PostgreSQL
7. Response sent back to Android

---

## ⚠️ Error Handling Strategy

| Scenario       | Handling                            |
| -------------- | ----------------------------------- |
| Blurry Image   | User-friendly error message         |
| AI Timeout     | Retry + graceful fallback           |
| Invalid Input  | Validation error response           |
| Server Failure | Controlled response (no raw errors) |

---

## 🔐 Security Approach

* API keys managed via environment variables
* No hardcoded secrets
* Configurable via `application.yml`

**Example:**

```
gemini:
  api-key: ${GEMINI_API_KEY}
```

---

## ⚡ Performance Optimizations

* DB-first lookup reduces API calls
* Image hashing avoids duplicate processing
* Reactive WebClient improves scalability
* Timeout and retry mechanisms

---

## 📱 Android UX Highlights

* 📤 Image upload interface
* ⏳ Loading indicator during processing
* ⚠️ Clear error messages
* 🎥 Display recommended exercise videos

---

## 🧪 Evaluation Criteria Coverage

| Requirement      | Implementation                  |
| ---------------- | ------------------------------- |
| Security Test    | No API key exposure             |
| Quota Test       | DB-first logic implemented      |
| Concurrency Test | WebClient used                  |
| UI Test          | Loading indicator + UX handling |

---

## ▶️ How to Run

### 🔹 Backend

1. Set environment variable:

```
export GEMINI_API_KEY=your_api_key
```

2. Run Spring Boot app:

```
mvn spring-boot:run
```

---

### 🔹 Database

* Install PostgreSQL
* Create database
* Update credentials in `application.yml`

---

### 🔹 Android App

1. Open in Android Studio
2. Update backend API URL
3. Run on emulator/device

---

## 📈 Future Enhancements

* 🔍 OCR integration for better text extraction
* 📊 Analytics dashboard
* 🧠 AI model fine-tuning
* 🔐 JWT-based authentication
* ☁️ Deployment on AWS (EKS / ECS)

---

## 💡 Design Decisions

* **WebClient over RestTemplate** → Non-blocking, scalable
* **DB-first approach** → Cost-efficient & faster response
* **Environment variables** → Secure secret management
* **Global exception handling** → Clean API responses

---

## 👨‍💻 Author

**Anusha Pattapu**


---

## ⭐ Summary

This project showcases the ability to build **secure, scalable, and production-ready systems** by combining:

* Smart caching strategies
* Reactive programming
* Clean architecture
* User-focused design

---
