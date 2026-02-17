import type { ReactNode } from "react";

import PrimaryButton from "../../ui/PrimaryButton/PrimaryButton";
import SecondaryButton from "../../ui/SecondaryButton/SecondaryButton";
import HighlightButton from "../../ui/HighlightButton/HighlightButton";
import "./styles.css";

type Action = {
  label: string;
  variant?: "primary" | "secondary" | "highlight";
  onClick?: () => void;
  icon?: ReactNode; // melhor que string
  disabled?: boolean;
};

type Props = {
  title: string;

  // ✅ compatibilidade com o padrão antigo
  subtitle?: string;
  actionLabel?: string;
  onAction?: () => void | Promise<void>;

  // ✅ novo padrão
  actions?: Action[];
  rightSlot?: ReactNode;
};

export default function PageHeader({
  title,
  subtitle,
  actionLabel,
  onAction,
  actions = [],
  rightSlot,
}: Props) {
  const legacyAction: Action | null =
    actionLabel && onAction
      ? { label: actionLabel, variant: "primary", onClick: () => void onAction() }
      : null;

  const finalActions = legacyAction ? [legacyAction, ...actions] : actions;

  return (
    <div className="mf-pageheader">
      <div className="mf-pageheader__left">
        <h1 className="mf-pageheader__title">{title}</h1>
        {subtitle ? <p className="mf-pageheader__subtitle">{subtitle}</p> : null}
      </div>

      <div className="mf-pageheader__right">
        {rightSlot ? <div className="mf-pageheader__slot">{rightSlot}</div> : null}

        {finalActions.length ? (
          <div className="mf-pageheader__actions">
            {finalActions.map((a) => {
              const content = (
                <>
                  {a.icon ? <span className="mf-pageheader__icon" aria-hidden="true">{a.icon}</span> : null}
                  <span>{a.label}</span>
                </>
              );

              const key = `${a.variant ?? "primary"}:${a.label}`;

              if (a.variant === "secondary") {
                return (
                  <SecondaryButton key={key} onClick={a.onClick} disabled={a.disabled || !a.onClick}>
                    {content}
                  </SecondaryButton>
                );
              }

              if (a.variant === "highlight") {
                return (
                  <HighlightButton key={key} onClick={a.onClick} disabled={a.disabled || !a.onClick}>
                    {content}
                  </HighlightButton>
                );
              }

              return (
                <PrimaryButton key={key} onClick={a.onClick} disabled={a.disabled || !a.onClick}>
                  {content}
                </PrimaryButton>
              );
            })}
          </div>
        ) : null}
      </div>
    </div>
  );
}