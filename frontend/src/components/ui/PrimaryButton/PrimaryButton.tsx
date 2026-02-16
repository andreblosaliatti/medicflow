import type { ButtonHTMLAttributes } from "react";
import "./style.css";

type Props = ButtonHTMLAttributes<HTMLButtonElement>;

export default function PrimaryButton({ className, ...props }: Props) {
  return (
    <button
      {...props}
      className={["mf-primary-button", className].filter(Boolean).join(" ")}
    >
      {props.children}
    </button>
  );
}
