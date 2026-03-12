import type { ReactNode } from "react";
import "./styles.css";

type Props = {
  children: ReactNode;
  className?: string;
};

export default function Card({ children, className = "" }: Props) {
  return (
    <div className={`mf-card ${className}`}>
      {children}
    </div>
  );
}