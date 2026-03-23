const CONSULTAS_CHANGED_EVENT = "mf:consultas-changed";

export function notifyConsultasChanged() {
  window.dispatchEvent(new Event(CONSULTAS_CHANGED_EVENT));
}

export function subscribeConsultasChanged(listener: () => void) {
  window.addEventListener(CONSULTAS_CHANGED_EVENT, listener);
  return () => window.removeEventListener(CONSULTAS_CHANGED_EVENT, listener);
}
