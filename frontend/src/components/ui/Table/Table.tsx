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

export function Tr({ children }: { children: ReactNode }) {
  return <tr>{children}</tr>;
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
}: {
  children: ReactNode;
  className?: string;
  align?: "left" | "right" | "center";
}) {
  const cls = ["mf-td", className ?? "", align ? `mf-td--${align}` : ""].join(" ").trim();
  return <td className={cls}>{children}</td>;
}