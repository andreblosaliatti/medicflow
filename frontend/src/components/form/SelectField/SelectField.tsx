import { useEffect, useMemo, useRef, useState } from "react";
import "./style.css";

export type SelectOption<T extends string | number> = {
  value: T;
  label: string;
};

type Props<T extends string | number> = {
  value: T | null;
  onChange: (value: T) => void;
  options: readonly SelectOption<T>[];

  placeholder?: string;
  label?: string;
  disabled?: boolean;
  ariaLabel?: string;
  className?: string;
};

export default function SelectField<T extends string | number>({
  value,
  onChange,
  options,
  placeholder = "Selecionar...",
  label,
  disabled = false,
  ariaLabel,
  className = "",
}: Props<T>) {
  const [open, setOpen] = useState(false);
  const rootRef = useRef<HTMLDivElement | null>(null);

  const selectedOption = useMemo(() => {
    if (value === null) return null;
    return options.find((o) => o.value === value) ?? null;
  }, [options, value]);

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (!rootRef.current) return;
      if (!rootRef.current.contains(e.target as Node)) setOpen(false);
    }

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  function toggle() {
    if (!disabled) setOpen((prev) => !prev);
  }

  function close() {
    setOpen(false);
  }

  function handleKeyDown(e: React.KeyboardEvent<HTMLButtonElement>) {
    if (e.key === "Escape") close();

    if (e.key === "Enter" || e.key === " ") {
      e.preventDefault();
      toggle();
    }
  }

  return (
    <div className={`mf-select ${className}`.trim()} ref={rootRef}>
      {label ? <label className="mf-select__label">{label}</label> : null}

      <button
        type="button"
        className={`mf-select__trigger ${open ? "is-open" : ""}`}
        onClick={toggle}
        onKeyDown={handleKeyDown}
        aria-haspopup="listbox"
        aria-expanded={open}
        aria-label={ariaLabel}
        disabled={disabled}
      >
        <span className={`mf-select__value ${!selectedOption ? "is-placeholder" : ""}`}>
          {selectedOption ? selectedOption.label : placeholder}
        </span>

        <span className="mf-select__chevron" aria-hidden="true">
          ▾
        </span>
      </button>

      {open && !disabled ? (
        <div className="mf-select__dropdown" role="listbox">
          {options.map((option) => (
            <button
              key={String(option.value)}
              type="button"
              role="option"
              aria-selected={option.value === value}
              className={`mf-select__option ${option.value === value ? "is-active" : ""}`}
              onClick={() => {
                onChange(option.value);
                close();
              }}
            >
              {option.label}
            </button>
          ))}
        </div>
      ) : null}
    </div>
  );
}