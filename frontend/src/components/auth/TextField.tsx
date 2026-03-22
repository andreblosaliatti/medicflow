// src/components/auth/TextField.tsx

import type { ReactNode, HTMLInputTypeAttribute } from "react";
import "./auth.css";

type Props = {
  value: string;
  onChange: (value: string) => void;
  placeholder: string;
  name?: string;
  type?: HTMLInputTypeAttribute;
  autoComplete?: string;
  leftIcon?: ReactNode;
  ariaLabel: string;
};

export default function TextField({
  value,
  onChange,
  placeholder,
  type = "text",
  autoComplete,
  leftIcon,
  ariaLabel,
  name,
}: Props) {
  return (
    <label className="field">
      <span className="field-icon" aria-hidden="true">
        {leftIcon}
      </span>

      <input
        className="field-input"
        name={name}
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        autoComplete={autoComplete}
        aria-label={ariaLabel}
      />
    </label>
  );
}