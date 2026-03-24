import { useEffect, useId, useMemo, useRef, useState } from "react";

import Input from "../Input";
import styles from "./styles.module.css";

export type AutocompleteOption<T extends string | number> = {
  value: T;
  label: string;
  description?: string;
};

type Props<T extends string | number> = {
  value: string;
  onChange: (value: string) => void;
  onSelect: (option: AutocompleteOption<T>) => void;
  options: readonly AutocompleteOption<T>[];
  placeholder?: string;
  label?: string;
  disabled?: boolean;
  ariaLabel?: string;
  emptyMessage?: string;
  loading?: boolean;
};

export default function AutocompleteField<T extends string | number>({
  value,
  onChange,
  onSelect,
  options,
  placeholder,
  label,
  disabled = false,
  ariaLabel,
  emptyMessage = "Nenhuma sugestão encontrada.",
  loading = false,
}: Props<T>) {
  const autoId = useId();
  const listboxId = `${autoId}-listbox`;
  const rootRef = useRef<HTMLDivElement | null>(null);
  const [open, setOpen] = useState(false);

  const showDropdown = useMemo(() => {
    if (disabled || !open) return false;
    if (!value.trim()) return false;
    return loading || options.length > 0 || Boolean(emptyMessage);
  }, [disabled, emptyMessage, loading, open, options.length, value]);

  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (!rootRef.current?.contains(event.target as Node)) {
        setOpen(false);
      }
    }

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <div className={styles.root} ref={rootRef}>
      <Input
        value={value}
        onChange={(event) => {
          onChange(event.target.value);
          setOpen(true);
        }}
        onFocus={() => setOpen(true)}
        placeholder={placeholder}
        label={label}
        aria-label={ariaLabel}
        aria-expanded={showDropdown}
        aria-controls={showDropdown ? listboxId : undefined}
        aria-autocomplete="list"
        autoComplete="off"
        disabled={disabled}
      />

      {showDropdown ? (
        <div className={styles.dropdown} role="listbox" id={listboxId}>
          {loading ? <div className={styles.status}>Buscando...</div> : null}

          {!loading && options.length === 0 ? <div className={styles.status}>{emptyMessage}</div> : null}

          {!loading
            ? options.map((option) => (
                <button
                  key={String(option.value)}
                  type="button"
                  className={styles.option}
                  onMouseDown={(event) => event.preventDefault()}
                  onClick={() => {
                    onSelect(option);
                    setOpen(false);
                  }}
                >
                  <span className={styles.optionLabel}>{option.label}</span>
                  {option.description ? <span className={styles.optionDescription}>{option.description}</span> : null}
                </button>
              ))
            : null}
        </div>
      ) : null}
    </div>
  );
}
