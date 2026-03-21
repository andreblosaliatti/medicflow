# Política de exclusão por agregado

## Objetivo

Padronizar o comportamento de exclusão para evitar divergência entre regra de negócio, API e persistência.

## Regra oficial

### Agregados com retenção obrigatória

Os agregados abaixo **não devem ser removidos fisicamente** em operações de negócio normais. O `DELETE` da API passa a significar **inativação (soft-delete)**, implementada com `ativo = false`.

- **Paciente**: sempre inativação, porque histórico clínico, consultas, prescrições e exames precisam continuar íntegros.
- **Usuário**: sempre inativação, para preservar trilha de auditoria, autenticação e vínculos operacionais.
- **Médico**: sempre inativação, para manter histórico assistencial, agenda e relacionamento com consultas anteriores.

## Regras operacionais

- Consultas de listagem padrão devem retornar apenas registros com `ativo = true`.
- Buscas por identificador usadas no fluxo principal devem considerar apenas registros ativos.
- Validações de relacionamento para criar consultas, exames e prescrições devem aceitar somente paciente e médico ativos.
- Registros inativos só devem aparecer em endpoints, relatórios ou rotinas explicitamente desenhados para isso.

## Implementação atual no backend

- `PacienteService`, `UsuarioService` e `MedicoService` inativam entidades no método `delete`.
- `PacienteRepository`, `UsuarioRepository` e `MedicoRepository` expõem consultas explícitas para ativos.
- Fluxos dependentes (`ConsultaService`, `ExameSolicitadoService` e `MedicamentoService`) validam paciente/médico ativo antes de operar.

## Fora do escopo desta política

Exclusão física só deve existir em rotinas administrativas/migração explicitamente aprovadas, nunca como comportamento padrão da API transacional.
