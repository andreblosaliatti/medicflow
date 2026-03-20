# Backend Technical Review (Spring Boot)

## Executive Summary

Overall backend health is **early-production / pre-hardening**. Core layering (Controller → Service → Repository) exists, but there are blocking issues in security, validation consistency, and query correctness that must be addressed before multi-tenant/SaaS scaling.

## Findings by Severity

### CRITICAL

1. **All endpoints are publicly accessible (authentication effectively disabled).**
   - `anyRequest().permitAll()` is configured in the active security chain.
   - JWT filter/authentication manager is not present in the codebase.

2. **Credentials are stored/seeded in plain text.**
   - `data.sql` inserts users with raw passwords (e.g., `admin123`, `senhaSegura#1`).
   - `UsuarioService` falls back to storing plain-text password when `PasswordEncoder` is absent.

3. **Broken domain query in medication listing can leak incorrect data.**
   - `listarMedicamentos(Long consultaId, ...)` validates `consultaId`, but calls `findByConsultaPacienteId(consultaId, ...)` (patient-id query), causing semantic mismatch and potentially wrong records.

4. **Build/runtime profile hygiene issues create unstable production behavior.**
   - `spring.profiles.active=test` in default `application.properties` and malformed property line (`spring.jpa.open-in-view=false# ...`) increase configuration drift risk.

### HIGH

1. **No Flyway migrations despite architecture requirement and mutable schema in runtime (`ddl-auto=update`).**
2. **N+1 risk in `listarTodosMedicosComPacientes` (1 query for medics + 1 query per medic for patients).**
3. **Unsafe DTOs coupling API contract to JPA entities (`Set<Role>` in input/output update DTOs).**
4. **Exception messages and semantics inconsistent (`"Recurso encontrado"` on not-found paths).**
5. **Inconsistent package/file conventions (`SecutityBeansConfig` typo, empty `SecurityConfig`, service package mismatch) harm maintainability and onboarding.**

### MEDIUM

1. **Soft-delete and hard-delete coexist without uniform policy (Paciente/Medico/Usuario).**
2. **Validation gaps in update DTOs (unused imports, weak invariants, no cross-field rules for teleconsultation/payment states).**
3. **Error response model is partial (no fallback 500 handler, no trace/correlation id, no standardized machine error code).**
4. **Potential null-path bug in embedded address update path (`this.endereco.atualizarInformacoes(...)`).**

### LOW

1. Duplicate update mapping logic in services (manual field-by-field copy methods repeated).
2. Minor naming/typo issues (`MedicamentoBaseRespository`, misspellings in messages/comments).
3. Test suite is minimal and partially miswired, providing low confidence for regressions.

## SaaS Scalability Risks

- **Auth/Authz gap:** no tenant/user boundary enforcement currently.
- **Query amplification:** per-doctor patient lookup loop will degrade under high doctor counts.
- **Schema drift:** without migration history, blue/green deploys and rollback safety are weak.
- **Observability gap:** actuator exists, but no request correlation / structured audit logging for sensitive operations.

## Incremental Improvement Plan

1. Lock down SecurityFilterChain (`authenticated()` by default), introduce JWT filter + stateless session.
2. Remove plain-text password fallback and seed only encoded passwords.
3. Fix medication repository method usage (`consultaId` vs `pacienteId`) and add contract tests.
4. Introduce Flyway baseline and disable `ddl-auto` outside local dev.
5. Add `@EntityGraph`/fetch-join strategy for doctor→patients use case to remove N+1.
6. Standardize API errors with machine code + trace id + global fallback handler.
7. Add focused tests for service rules, repository queries, and security integration.
