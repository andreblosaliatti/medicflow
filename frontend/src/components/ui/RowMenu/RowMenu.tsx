import { useEffect, useRef } from "react";
import "./styles.css";

export type RowMenuItem = {
  key: string;
  label: string;
  icon?: string;
  tone?: "default" | "primary" | "danger";
  description?: string;
};

type Props = {
  open: boolean;
  onClose: () => void;
  items: RowMenuItem[];
  onSelect: (key: string) => void;
};

export default function RowMenu({ open, onClose, items, onSelect }: Props) {
  const ref = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (!open) return;

    function onDocClick(e: MouseEvent) {
      const el = ref.current;
      if (!el) return;
      if (e.target instanceof Node && !el.contains(e.target)) onClose();
    }

    document.addEventListener("click", onDocClick);
    return () => document.removeEventListener("click", onDocClick);
  }, [open, onClose]);

  if (!open) return null;

  return (
    <div className="mf-rowmenu" ref={ref} role="menu">
      {items.map((it) => {
        const cls = [
          "mf-rowmenu__item",
          it.tone === "primary" ? "mf-rowmenu__item--primary" : "",
          it.tone === "danger" ? "mf-rowmenu__item--danger" : "",
        ]
          .join(" ")
          .trim();

        return (
          <button
            key={it.key}
            type="button"
            className={cls}
            onClick={(e) => {
              e.stopPropagation();
              onSelect(it.key);
              onClose();
            }}
            role="menuitem"
          >
            <span className="mf-rowmenu__ico" aria-hidden="true">
              {it.icon ?? "•"}
            </span>

            <span className="mf-rowmenu__text">
              <span className="mf-rowmenu__title">{it.label}</span>
              {it.description ? <span className="mf-rowmenu__sub">{it.description}</span> : null}
            </span>
          </button>
        );
      })}
    </div>
  );
}