# SkillBridge AI — AI-Powered Adaptive Learning Platform with Personalized Skill-Gap Analysis

[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot 3.3.5](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL 8.4](https://img.shields.io/badge/MySQL-8.4-blue.svg)](https://www.mysql.com/)
[![Docker Compose](https://img.shields.io/badge/Docker-Compose-2496ED.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-Academic%20Use-lightgrey.svg)]()

SkillBridge AI transforms assessment-based career evaluation into an end-to-end **Personalized Learning & Skill-Gap Remediation Platform**. Students assess their domain knowledge across 6 tech careers, identify precise skill gaps against industry benchmarks, enroll in structured theory courses, master key concepts with executable code examples, take automated end-of-lesson mastery quizzes, and follow an adaptive milestone learning roadmap.

---

## Key Features

### 1. Career-Based Multi-Skill Assessments
- **6 Tech Career Tracks:** Java Developer, Full Stack Developer, Data Scientist, Cloud Engineer, AI/ML Engineer, and Cybersecurity Analyst.
- **Adaptive Question Sequencing:** Dynamic question delivery adapted to the student's recent career readiness level.
- **Automated Multi-Skill Scoring:** Calculates benchmark scores, target score comparisons, and percentage gap identification per skill.

### 2. Comprehensive Theory Course Catalog (LMS)
- **32 Full Courses:** Pre-seeded curriculum spanning core subjects across all 6 careers.
- **Search & Filters:** Real-time search by keywords, filter by career track, and filter by difficulty (`BEGINNER`, `INTERMEDIATE`, `ADVANCED`).
- **Course Metrics:** Duration estimates, lesson counters, skill tags, and live enrollment/completion badges.

### 3. In-Depth Theory Lessons
- **64 Structured Lessons:** In-depth educational content designed for college students and aspiring software engineers (no placeholder text).
- **Rich Lesson Structure:**
  - *Learning Objectives:* Clear milestone checklist.
  - *Introduction:* Real-world architectural motivation.
  - *Detailed Theory Explanations:* Concepts, diagrams, and industry best practices.
  - *Runnable Code Examples:* Valid, syntax-highlighted code in Java, Python, JavaScript, SQL, and Bash with conceptual breakdowns.
  - *Key Concepts & Takeaways:* Highlights and pitfalls to avoid.
  - *Lesson Summary & Practice Questions:* Self-reflection questions and critical-thinking prompts.
- **Interactive Reader UI:** Syllabus sidebar with completion indicators, previous/next lesson navigation, and auto-resume.

### 4. Interactive End-of-Lesson Quizzes
- **128 Multiple-Choice Questions:** 4 options per question with exactly 1 correct answer.
- **Instant Automatic Grading:** 70% passing threshold with real-time score calculation.
- **Immediate Answer Explanations:** Detailed conceptual explanations displayed upon submission.
- **Progress Synchronization:** Passing a quiz automatically marks the lesson as complete and updates course progress.
- **Audit Persistence:** Quiz attempts and answer histories are safely persisted to MySQL.

### 5. Gap-Prioritized Personalized Recommendations
- **Direct Skill-Gap Integration:** Evaluates the student's latest assessment results to identify weak skills.
- **Algorithmic Ranking:** Courses are prioritized in descending order of skill gap magnitude.
- **Actionable Rationale:** Clear explanations explaining why each course is recommended (e.g., *"You scored 35% in SQL (target 80%). We recommend SQL Fundamentals because it addresses your largest skill gap of 45%."*).

### 6. Milestone Learning Roadmap
- **Customized Path:** Milestone sequence organizing recommended courses into prioritized learning phases.
- **Overall Completion Tracking:** Tracks progress across all roadmap milestones.
- **Skill Mastery Integrity:** Reassessment prompts encourage students to retake career assessments to objectively measure improvement.

### 7. Role-Protected Admin Course Management
- **Role-Based Access Control (RBAC):** Admin endpoints are protected by Spring Security (`ROLE_ADMIN`). Non-admin attempts return HTTP 403 Forbidden.
- **Content Management:** Create, update, publish/unpublish courses, manage lessons, and author quiz questions.

---

## Tech Stack & Architecture

- **Backend:** Java 17, Spring Boot 3.3.5, Spring Security 6.3.4, Spring Data JPA, Hibernate ORM 6.5.3, JJWT 0.12.5.
- **Database:** MySQL 8.4 (Docker) / H2 in-memory (Test fixtures).
- **Frontend:** Vanilla HTML5, CSS3, JavaScript (ES6+), Bootstrap 5.3.3, Font Awesome 6.4.0, Chart.js.
- **DevOps:** Docker multi-stage build (`eclipse-temurin:17-jre`), Docker Compose.

---

## Running with Docker Compose (Recommended)

1. Make sure **Docker Desktop** is running.
2. Open a terminal in the project root directory:
   ```powershell
   cd SkillBridge-AI-Java17
   ```
3. Copy the environment configuration:
   ```powershell
   Copy-Item .env.example .env
   ```
4. Build and start the containers:
   ```powershell
   docker compose up --build -d
   ```
5. Open your browser and navigate to:
   - **Web Application:** [http://localhost:8084](http://localhost:8084)
   - **Health Check:** [http://localhost:8084/actuator/health](http://localhost:8084/actuator/health)
   - **Course Catalog API:** [http://localhost:8084/api/courses](http://localhost:8084/api/courses)
   - **MySQL Database:** `localhost:3307` (`skillbridge_db` / user: `skillbridge` / pass: `skillbridge`)

To stop the containers:
```powershell
docker compose down
```

---

## Running Locally without Docker

1. Ensure **Java 17 (JDK)** and **Maven 3.8+** are installed.
2. Start a local MySQL instance and create a database named `skillbridge_db`.
3. Set your environment variables:
   ```powershell
   $env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/skillbridge_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true"
   $env:SPRING_DATASOURCE_USERNAME = "root"
   $env:SPRING_DATASOURCE_PASSWORD = "your_mysql_password"
   $env:JWT_SECRET = "SkillBridgeSecretKeyForJwtSigningMustBeAtLeast256BitsLongForHS256Algorithm"
   ```
4. Run the application:
   ```powershell
   mvn spring-boot:run
   ```
5. Access the application at [http://localhost:8080](http://localhost:8080).

---

## Default Demo Credentials

Pre-seeded accounts for testing and demonstration:

| Role | Email | Password | Permissions |
| :--- | :--- | :--- | :--- |
| **Student** | `student@skillbridge.ai` | `Student@123` | Assessments, Course Enrollment, Quizzes, Progress, Roadmap |
| **Admin** | `admin@skillbridge.ai` | `Admin@123` | All Student Features + Admin Course/Lesson Management & Analytics |

---

## Key API Endpoints

### Authentication & Health
- `POST /api/auth/register` — Student registration
- `POST /api/auth/login` — Student/Admin authentication (returns JWT token)
- `GET /api/health` — Application health status

### Courses & Learning Management System (LMS)
- `GET /api/courses` — Search and filter course catalog (Public)
- `GET /api/courses/{idOrSlug}` — Course details with lesson syllabus (Public/Authenticated)
- `GET /api/courses/my-enrollments` — Authenticated student's active course enrollments
- `POST /api/courses/{idOrSlug}/enroll` — Enroll authenticated student into course
- `GET /api/courses/{courseId}/lessons/{lessonId}` — Lesson details, theory, and code examples
- `POST /api/courses/lessons/{lessonId}/complete` — Mark lesson as completed
- `GET /api/courses/lessons/{lessonId}/quiz` — Retrieve lesson quiz questions
- `POST /api/courses/lessons/{lessonId}/quiz` — Submit quiz answers for automated scoring
- `GET /api/courses/recommendations` — Personalized courses ranked by assessment skill gaps
- `GET /api/courses/roadmap` — Adaptive learning roadmap with milestones

### Career Assessments & Insights
- `GET /api/careers` — List all career tracks
- `GET /api/careers/{careerId}/questions` — Career assessment questions
- `GET /api/careers/{careerId}/adaptive-questions` — Adaptive assessment questions
- `POST /api/careers/{careerId}/assessments` — Submit career assessment
- `GET /api/careers/assessments/my` — Student career assessment history
- `GET /api/student/insights` — Student analytics and proficiency scores

### Admin Management (`ROLE_ADMIN` Required)
- `GET /api/admin/career-overview` — Platform assessment statistics
- `GET /api/admin/career-assessments` — All student assessment records
- `POST /api/admin/courses` — Create new course
- `PUT /api/admin/courses/{id}` — Update existing course
- `DELETE /api/admin/courses/{id}` — Delete course
- `POST /api/admin/courses/{id}/toggle-publish` — Toggle publication status
- `POST /api/admin/courses/{id}/lessons` — Add lesson to course
- `PUT /api/admin/courses/lessons/{lessonId}` — Edit lesson content
- `DELETE /api/admin/courses/lessons/{lessonId}` — Delete lesson
- `PUT /api/admin/courses/lessons/{lessonId}/quiz` — Set quiz questions

---

## Automated Test Suite

Run the full automated test suite (54 unit and integration tests):
```powershell
mvn clean test
```

All 54 tests pass:
- `AdminCourseControllerTest` (Admin CRUD and authorization)
- `ApiControllerTest` (Assessment validation and scoring)
- `AuthControllerTest` (Registration and login security)
- `CourseCatalogControllerTest` (Course catalog, enrollment, syllabus, and quiz APIs)
- `AuthorizationAndAccessTest` (Role enforcement and data isolation)
- `CourseRecommendationAndRoadmapTest` (Skill-gap prioritization and milestone sequence)
- `SkillGapServiceTest` (Readiness evaluation and target score analysis)
- `SkillBridgeApplicationTests` (Spring context bootstrap)
