import { startRouter, renderRoute } from "./router/router.js";

function isAuthenticated() {
  return Boolean(localStorage.getItem("mf_token"));
}

function bindGlobalEvents() {
  document.addEventListener("click", (e) => {
    const target = e.target;

    // Logout (no shell)
    if (target && target.id === "btnLogout") {
      localStorage.removeItem("mf_token");
      window.location.hash = "#/login";
    }
  });
}

async function render() {
  try {
    await renderRoute({ isAuthenticated });
    bindGlobalEvents();
  } catch (err) {
    console.error(err);
    const app = document.getElementById("app");
    app.innerHTML = `<div style="padding:24px;font-family:Arial">Erro carregando tela: ${err.message}</div>`;
  }
}

startRouter(render);

// DEV: para “simular login”, execute no console:
// localStorage.setItem("mf_token","dev"); location.hash="#/dashboard";
