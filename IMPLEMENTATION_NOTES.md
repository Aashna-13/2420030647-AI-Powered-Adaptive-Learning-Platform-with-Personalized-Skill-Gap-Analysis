# Update notes — Phase 4 to Phase 8

This update was applied to the uploaded SkillBridge-AI-Java17 project rather than a fresh starter.

## Added
- Student `/api/student/insights` endpoint: readiness trend and skill-level averages scoped to the authenticated user.
- Student `/api/student/roadmap` endpoint: prioritized learning actions derived from the latest career assessment and curated resource links.
- Optional server-side Gemini study-coach advice. Configure `GEMINI_API_KEY` in `.env`; the deterministic roadmap remains available if no key is configured or the provider is unavailable.
- Adaptive assessment question ordering based on the user's latest career readiness. The full assessment remains mandatory; this preserves score integrity and the existing incomplete-submission validation.
- Student dashboard analytics chart, skill averages, learning roadmap cards, and admin-only dashboard UI.
- Admin `/api/admin/career-overview` and `/api/admin/career-assessments` endpoints. They are protected by Spring Security's ADMIN role check and return summary DTO-like maps rather than raw JPA entities.
- README updates for Java 17, Docker port mapping, endpoints, demo credentials, and optional AI configuration.

## Phase mapping
- Phase 4: progress analytics and skill visualization.
- Phase 5: personalized learning roadmap plus optional Gemini coach.
- Phase 6: adaptive assessment ordering and gap-based recommendations.
- Phase 7: admin reporting dashboard and role-protected APIs.
- Phase 8: integration/documentation/Docker configuration review.

## Validation performed in this environment
- All frontend JavaScript files passed `node --check`.
- `pom.xml` parsed as valid XML.
- Source was reviewed for Java 17-compatible syntax and dependencies.
- Maven and Docker are not installed in this environment, so the new Java code and Docker image could not be compiled/run here. Run `mvn clean test`, `mvn clean package`, and `docker compose up --build` on your computer before presenting/submitting.

## Important
- The roadmap engine is deterministic and based on actual assessment gaps. Gemini advice is optional and requires a valid API key, network access, quota, and available model access.
- Do not publish `.env`, API keys, or the demo credentials as production credentials.
