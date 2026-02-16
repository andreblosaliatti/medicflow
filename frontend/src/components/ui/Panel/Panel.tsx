import type { ReactNode } from "react";
import "./styles.css";

type Props = {
  title: string;
  icon?: ReactNode;
  right?: ReactNode;
  children: ReactNode;
  className?: string;
};

export default function Panel({ title, icon, right, children, className }: Props) {
  return (
    <section className={`mf-panel ${className ?? ""}`}>
      <div className={`mf-panel__head ${right ? "mf-panel__head--between" : ""}`}>
        <div className="mf-panel__title">
          {icon ? (
            <span className="mf-panel__icon" aria-hidden="true">
              {icon}
            </span>
          ) : null}
          <span>{title}</span>
        </div>

        {right ? <div className="mf-panel__right">{right}</div> : null}
      </div>

      <div className="mf-panel__body">{children}</div>
    </section>
  );
}