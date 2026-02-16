import type { ButtonHTMLAttributes } from "react";
import "./style.css";

type Props = ButtonHTMLAttributes<HTMLButtonElement>;

export default function SecondaryButton({ className, ...props }: Props) {
  return (
    <button
      {...props}
      className={["mf-secondary-button", className].filter(Boolean).join(" ")}
    >
      {props.children}
    </button>
  );
}
