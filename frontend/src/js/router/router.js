import { routes } from "./routes.js";
import { setActiveNav } from "../utils/dom.js";

function getHashPath() {
  const hash = window.location.hash || "#/login";
  return hash.replace("#", "");
}

function matchRoute(path) {
  // match exato
  if (routes[path]) return { template: routes[path], params: {} };

  // match com :id
  const pathParts = path.split("/").filter(Boolean);

  for (const routePattern of Object.keys(routes)) {
    const patternParts = routePattern.split("/").filter(Boolean);

    if (patternParts.length !== pathParts.length) continue;

    const params = {};
    let ok = true;

    for (let i = 0; i < patternParts.length; i++) {
      const p = patternParts[i];
      const v = pathParts[i];

      if (p.startsWith(":")) {
        params[p.slice(1)] = v;
      } else if (p !== v) {
        ok = false;
        break;
      }
    }

    if (ok) return { template: routes[routePattern], params };
  }

  return null;
}

async function loadHTML(url) {
  const res = await fetch(url);
  if (!res.ok) throw new Error(`Falha ao carregar ${url}`);
  return res.text();
}

export async function renderRoute({ isAuthenticated }) {
  const app = document.getElementById("app");
  const path = getHashPath();

  // proteção simples
  const publicRoutes = ["/login", "/forgot-password"];
  const isPublic = publicRoutes.includes(path);

  if (!isAuthenticated() && !isPublic) {
    window.location.hash = "#/login";
    return;
  }

  // Se logado e tentar abrir login, manda pro dashboard
  if (isAuthenticated() && path === "/login") {
    window.location.hash = "#/dashboard";
    return;
  }

  // Decide se usa shell ou não
  if (isPublic) {
    const viewHtml = await loadHTML(`views/pages${path}.html`);
    app.innerHTML = viewHtml;
    return;
  }

  // Shell
  const shellHtml = await loadHTML("views/shell.html");
  app.innerHTML = shellHtml;

  const matched = matchRoute(path) || { template: "views/pages/dashboard.html", params: {} };
  const view = document.getElementById("view");

  const pageHtml = await loadHTML(matched.template);
  view.innerHTML = pageHtml;

  // Marca menu ativo (usa o primeiro segmento)
  const navKey = path.split("/")[1] || "dashboard";
  setActiveNav(navKey);
}

export function startRouter(renderFn) {
  window.addEventListener("hashchange", renderFn);
  window.addEventListener("load", renderFn);
}
