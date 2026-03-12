import type { ReactNode } from "react";
import "./styles.css";

export function TableWrap({ children }: { children: ReactNode }) {
  return <div className="mf-table-wrap">{children}</div>;
}

export function Table({ children }: { children: ReactNode }) {
  return <table className="mf-table">{children}</table>;
}

export function THead({ children }: { children: ReactNode }) {
  return <thead>{children}</thead>;
}

export function TBody({ children }: { children: ReactNode }) {
  return <tbody>{children}</tbody>;
}

export function Tr({
  children,
  onClick,
  className,
  ariaLabel,
}: {
  children: ReactNode;
  onClick?: () => void;
  className?: string;
  ariaLabel?: string;
}) {
  const clickable = Boolean(onClick);
  const cls = [
    className ?? "",
    clickable ? "mf-tr--clickable" : "",
  ]
    .join(" ")
    .trim();

  return (
    <tr
      className={cls}
      onClick={onClick}
      role={clickable ? "button" : undefined}
      tabIndex={clickable ? 0 : undefined}
      aria-label={clickable ? ariaLabel : undefined}
      onKeyDown={
        clickable
          ? (e) => {
              if (e.key === "Enter" || e.key === " ") onClick?.();
            }
          : undefined
      }
    >
      {children}
    </tr>
  );
}

export function Th({
  children,
  style,
  align,
}: {
  children: ReactNode;
  style?: React.CSSProperties;
  align?: "left" | "right" | "center";
}) {
  return (
    <th style={style} className={align ? `mf-th mf-th--${align}` : "mf-th"}>
      {children}
    </th>
  );
}

export function Td({
  children,
  className,
  align,
  ...rest
}: {
  children: ReactNode;
  className?: string;
  align?: "left" | "right" | "center";
} & React.TdHTMLAttributes<HTMLTableCellElement>) {

  const cls = [
    "mf-td",
    className ?? "",
    align ? `mf-td--${align}` : "",
  ]
    .join(" ")
    .trim();

  return (
    <td className={cls} {...rest}>
      {children}
    </td>
  );
}