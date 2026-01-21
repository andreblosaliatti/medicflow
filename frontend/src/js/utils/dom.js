export function setActiveNav(key) {
  const items = document.querySelectorAll(".mf-nav__item");
  items.forEach((a) => {
    const isActive = a.dataset.nav === key;
    a.classList.toggle("is-active", isActive);
  });
}
