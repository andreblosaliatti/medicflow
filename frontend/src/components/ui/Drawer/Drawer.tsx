import { useEffect } from "react";
import styles from "./drawer.module.css";

type DrawerVariant = "default" | "primary";

type Props = {
  open: boolean;
  title?: string;
  onClose: () => void;
  children: React.ReactNode;
  width?: number;

  // ✅ sem gambiarras: permite customizar por tela
  variant?: DrawerVariant;
  headerClassName?: string;
  titleClassName?: string;
  closeClassName?: string;
};

export default function Drawer({
  open,
  title,
  onClose,
  children,
  width = 520,
  variant = "default",
  headerClassName,
  titleClassName,
  closeClassName,
}: Props) {
  useEffect(() => {
    function onKeyDown(e: KeyboardEvent) {
      if (e.key === "Escape") onClose();
    }
    if (open) window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [open, onClose]);

  if (!open) return null;

  const headerVariantClass = variant === "primary" ? styles.headerPrimary : styles.headerDefault;
  const titleVariantClass = variant === "primary" ? styles.titleOnPrimary : styles.titleDefault;
  const closeVariantClass = variant === "primary" ? styles.closeOnPrimary : styles.closeDefault;

  return (
    <div className={styles.overlay} onMouseDown={onClose} role="presentation">
      <aside
        className={styles.drawer}
        style={{ width }}
        onMouseDown={(e) => e.stopPropagation()}
        role="dialog"
        aria-modal="true"
      >
        <div className={`${styles.header} ${headerVariantClass} ${headerClassName ?? ""}`}>
          <div className={`${styles.title} ${titleVariantClass} ${titleClassName ?? ""}`}>
            {title ?? "Detalhes"}
          </div>

          <button
            type="button"
            className={`${styles.close} ${closeVariantClass} ${closeClassName ?? ""}`}
            onClick={onClose}
            aria-label="Fechar"
          >
            ✕
          </button>
        </div>

        <div className={styles.body}>{children}</div>
      </aside>
    </div>
  );
}