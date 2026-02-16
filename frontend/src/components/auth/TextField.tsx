// src/components/auth/TextField.tsx
import "./auth.css";

type Props = {
  value: string;
  onChange: (value: string) => void;
  placeholder: string;
  type?: React.HTMLInputTypeAttribute;
  autoComplete?: string;
  leftIcon?: React.ReactNode;
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
}: Props) {
  return (
    <label className="field">
      <span className="field-icon" aria-hidden="true">
        {leftIcon}
      </span>

      <input
        className="field-input"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        type={type}
        autoComplete={autoComplete}
        aria-label={ariaLabel}
      />
    </label>
  );
}
