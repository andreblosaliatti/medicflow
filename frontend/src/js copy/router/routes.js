export const routes = {
  "/login": "views/pages/login.html",
  "/forgot-password": "views/pages/forgot-password.html",

  "/dashboard": "views/pages/dashboard.html",
  "/agenda": "views/pages/agenda.html",
  "/agenda/novo": "views/pages/agenda-form.html",
  "/agenda/:id": "views/pages/agenda-detail.html",

  "/pacientes": "views/pages/pacientes.html",
  "/pacientes/novo": "views/pages/paciente-form.html",
  "/pacientes/:id": "views/pages/paciente-detail.html",
  "/pacientes/:id/consulta/nova": "views/pages/consulta.html",

  "/consultas": "views/pages/consultas.html",
  "/consultas/:id": "views/pages/consulta.html",

  "/prescricoes": "views/pages/prescricoes.html",
  "/comunicacao": "views/pages/comunicacao.html",

  "/usuarios": "views/pages/usuarios.html",
  "/usuarios/novo": "views/pages/usuario-form.html",
  "/usuarios/:id": "views/pages/usuario-form.html",

  "/configuracoes": "views/pages/configuracoes.html",
};
