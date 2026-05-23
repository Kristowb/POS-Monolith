# POS Monolith Monorepo

Repositori ini mengadopsi arsitektur decoupled monorepo modern yang memisahkan backend API (**Spring Boot 3**) dan frontend client (**Vue 3**) dalam satu tempat kerja untuk kolaborasi yang sangat erat.

## Panduan Arsitektur & Teknologi

```plaintext
d:\Code\AI\POS Monolith/
├── .github/workflows/deploy.yml   # CI/CD Pipeline (Maven & Vite build)
├── backend/                        # Backend RESTful (Spring Boot 3 + H2 Database)
└── frontend/                       # Frontend Web (Vue 3 + TS + Vite + Pinia)
```

### 1. REST API & Penanganan Error Standar (RFC 7807)
- **Spring Boot 3**: Menggunakan namespace `jakarta.*`.
- **Zalando Problem Spring Web**: Integrasi otomatis melalui dependensi `problem-spring-web` (0.29.1) dan `jackson-datatype-problem` (0.27.1).
- **Global Exception Handling**: Seluruh exception (validasi, duplikasi data, tidak ditemukan) ditangkap secara global dan diterjemahkan otomatis menjadi format JSON standar RFC 7807 (`ProblemDetail`) menggunakan `GlobalExceptionHandler` dan `JacksonConfig`.
- **Bisnis Logik**: Pengecualian dilempar menggunakan builder dinamis dari Zalando:
  ```java
  throw Problem.builder()
      .withType(URI.create("https://api.app.com/errors/user-not-found"))
      .withTitle("User Tidak Ditemukan")
      .withStatus(Status.NOT_FOUND)
      .withDetail("Pengguna dengan ID " + id + " tidak ditemukan.")
      .build();
  ```

### 2. Single Source of Truth (Otomatisasi Tipe TypeScript)
- **TypeScript Generator**: Menggunakan `typescript-generator-maven-plugin` (versi `3.2.1263`) di `backend/pom.xml`.
- **Siklus Generator**: Setiap kali backend dikompilasi dengan perintah Maven:
  ```bash
  mvn clean compile process-classes
  ```
  Plugin akan memindai DTO Java pada paket `com.app.dto.**` dan secara otomatis memperbarui file deklarasi tipe di frontend: `frontend/src/types/backend-models.d.ts`.
- **Integrasi Frontend**: Berkas `frontend/src/types/index.ts` mengimpor tipe hasil generate ini dan mengekspornya kembali untuk menjaga konsistensi tipe data di seluruh views, store, dan components.

### 3. Desain Frontend Vue 3
- **Premium Dark/Glassmorphism**: Didefinisikan secara global menggunakan CSS variables di `frontend/src/assets/main.css`.
- **RFC 7807 Axios Interceptor**: Terpasang di `frontend/src/services/api.ts` untuk memetakan error JSON dari backend secara langsung ke kolom form validasi secara dinamis di `UserForm.vue`.

---

*Catatan untuk AI Assistant*: Sesi awal penyiapan arsitektur ini terdokumentasi di berkas [walkthrough.md](file:///C:/Users/User/.gemini/antigravity/brain/abe5e70b-11d6-496f-a9cd-6a2cd97fdc72/walkthrough.md) dalam AppData Antigravity.
