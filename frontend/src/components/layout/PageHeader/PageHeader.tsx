import type { ReactNode } from "react";

import PrimaryButton from "../../ui/PrimaryButton/PrimaryButton";
import SecondaryButton from "../../ui/SecondaryButton/SecondaryButton";
import HighlightButton from "../../ui/HighlightButton/HighlightButton";
import "./styles.css";

type Action = {
  label: string;
  variant?: "primary" | "secondary" | "highlight";
  onClick?: () => void;
  icon?: string;
};

type Props = {
  title: string;
  actions?: Action[];
  rightSlot?: ReactNode; // ✅ NOVO (opcional)
};

export default function PageHeader({ title, actions = [], rightSlot }: Props) {
  return (
    <div className="mf-pageheader">
      <div className="mf-pageheader__left">
        <h1 className="mf-pageheader__title">{title}</h1>
      </div>

      <div className="mf-pageheader__right">
        {rightSlot ? <div className="mf-pageheader__slot">{rightSlot}</div> : null}

        <div className="mf-pageheader__actions">
          {actions.map((a, idx) => {
            const content = (
              <>
                {a.icon ? <span aria-hidden="true">{a.icon}</span> : null}
                <span>{a.label}</span>
              </>
            );

            if (a.variant === "secondary") {
              return (
                <SecondaryButton key={idx} onClick={a.onClick}>
                  {content}
                </SecondaryButton>
              );
            }

            if (a.variant === "highlight") {
              return (
                <HighlightButton key={idx} onClick={a.onClick}>
                  {content}
                </HighlightButton>
              );
            }

            return (
              <PrimaryButton key={idx} onClick={a.onClick}>
                {content}
              </PrimaryButton>
            );
          })}
        </div>
      </div>
    </div>
  );
}