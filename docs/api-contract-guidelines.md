# Contrato de API: erros, DTOs e OpenAPI

Este documento consolida as regras de contrato da API Medicflow para integração entre backend, frontend e consumidores externos.

## 1. OpenAPI é o contrato oficial

- A especificação OpenAPI publicada pela aplicação deve refletir os **DTOs reais**, os **status codes reais** e os **schemas reais** retornados pela API.
- Mudanças em payload, códigos de erro, campos obrigatórios ou semântica de resposta devem ser tratadas como **mudanças de contrato**.
- Sempre que um endpoint novo for criado ou um endpoint existente mudar, a saída de `/v3/api-docs` deve permanecer consistente com:
  - DTO de formulário do caso de uso;
  - DTO de listagem ou detalhe retornado;
  - respostas de erro documentadas.

## 2. Diferença entre `400` e `422`

### `400 Bad Request`

Use `400` quando a requisição não puder ser interpretada corretamente no nível de transporte/serialização/parametrização:

- query params inválidos;
- path params inválidos;
- enum inválido em parâmetro;
- corpo JSON malformado;
- tipo incompatível durante desserialização;
- violações de validação em parâmetros de método (`@RequestParam`, `@PathVariable`, etc.).

Código estável de contrato:

- `CORE-REQUEST-400`

### `422 Unprocessable Entity`

Use `422` quando o JSON é sintaticamente válido, mas o **payload de formulário** viola o contrato do caso de uso:

- campo obrigatório ausente no body;
- formato inválido em campo do body;
- tamanho máximo/mínimo inválido;
- combinações inválidas detectadas nas validações do payload.

Código estável de contrato:

- `CORE-VALIDATION-422`

### Regras práticas

- `400` = problema para **entender** a requisição.
- `422` = problema para **aceitar** o conteúdo do body já entendido.

## 3. Convenção de códigos de erro por domínio

Os códigos de erro seguem o padrão:

`DOMINIO-TIPO-HTTP`

Exemplos:

- `PACIENTE-NOT-FOUND-404`
- `CONSULTA-BUSINESS-422`
- `EXAME-BASE-NOT-FOUND-404`
- `MEDICAMENTO-BUSINESS-422`
- `AUTH-ACCESS-DENIED-403`
- `CORE-INTERNAL-500`

### Diretrizes

- O código deve ser **estável para integrações**.
- A mensagem pode evoluir para fins de UX, mas o código deve continuar identificando a categoria do erro.
- Erros de domínio devem usar o domínio correspondente (`PACIENTE`, `CONSULTA`, `EXAME`, `MEDICAMENTO`, `USUARIO`, `AUTH`, `CORE`).

## 4. Convenção de DTOs por caso de uso

Os DTOs devem explicitar o papel do contrato no caso de uso:

- **Formulário**: payload de entrada para criação/edição.
- **Listagem**: payload resumido para grids, tabelas e buscas.
- **Detalhe**: payload expandido para leitura individual/perfil.

### Exemplos atuais

#### Paciente

- `PacienteFormDTO` → cadastro completo.
- `PacienteUpdateFormDTO` → atualização.
- `PacienteListDTO` → listagens.
- `PacienteDetailDTO` → perfil/detalhe.

#### Consulta

- `ConsultaFormDTO` → cadastro.
- `ConsultaUpdateFormDTO` → atualização.
- `ConsultaListDTO` → listagens.
- `ConsultaDetailDTO` → detalhe.

## 5. Resposta padrão de erro

Toda resposta de erro deve manter a estrutura:

- `timestamp`
- `status`
- `error`
- `code`
- `message`
- `path`
- `traceId`

Para validações, acrescentar:

- `errors[]` com `fieldName` e `message`

## 6. Checklist para evolução de contrato

Antes de concluir uma mudança de endpoint:

1. Confirmar o DTO de formulário/listagem/detalhe correto.
2. Confirmar se o erro esperado é `400`, `404`, `422` ou outro status.
3. Confirmar se o `code` do erro está no domínio correto.
4. Confirmar se a OpenAPI expõe o mesmo contrato retornado em runtime.
