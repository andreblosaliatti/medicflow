import type { ButtonHTMLAttributes } from "react";
import "./style.css";

type Props = ButtonHTMLAttributes<HTMLButtonElement>;

export default function HighlightButton({ className, ...props }: Props) {
  return (
    <button
      {...props}
      className={["mf-highlight-button", className].filter(Boolean).join(" ")}
    >
      {props.children}
    </button>
  );
}
