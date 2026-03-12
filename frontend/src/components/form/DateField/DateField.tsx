import { useEffect, useId, useRef, useState } from "react";
import "./styles.css";

type Props = {
  value: string;
  onChange: (value: string) => void;

  id?: string;
  name?: string;
  disabled?: boolean;

  min?: string;
  max?: string;
  "aria-label"?: string;
};

export default function DateField({
  value,
  onChange,
  id,
  name,
  disabled,
  min,
  max,
  "aria-label": ariaLabel,
}: Props) {
  const autoId = useId();
  const inputId = id ?? `date-${autoId}`;

  const inputRef = useRef<HTMLInputElement | null>(null);
  const [pickerOpen, setPickerOpen] = useState(false);

  useEffect(() => {
    const el = inputRef.current;
    if (!el) return;

    const onFocus = () => setPickerOpen(true);
    const onBlur = () => setPickerOpen(false);

    el.addEventListener("focus", onFocus);
    el.addEventListener("blur", onBlur);
    return () => {
      el.removeEventListener("focus", onFocus);
      el.removeEventListener("blur", onBlur);
    };
  }, []);

  function togglePicker(e: React.MouseEvent) {
    e.preventDefault();
    e.stopPropagation();

    const el = inputRef.current;
    if (!el || disabled) return;

    if (pickerOpen) {
      el.blur();
      setPickerOpen(false);
      return;
    }

    el.focus();
    type InputWithPicker = HTMLInputElement & {
        showPicker?: () => void;
        };

        const input = el as InputWithPicker;

        if (typeof input.showPicker === "function") {
        input.showPicker();
        }
    }

  return (
    <div className="mf-dtWrap">
      <input
        ref={inputRef}
        id={inputId}
        name={name}
        className="mf-dtInput"
        type="date"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        disabled={disabled}
        min={min}
        max={max}
        aria-label={ariaLabel}
      />

      <button
        type="button"
        className="mf-dtBtn"
        onMouseDown={(e) => e.preventDefault()}
        onClick={togglePicker}
        aria-label={pickerOpen ? "Fechar calendário" : "Abrir calendário"}
        disabled={disabled}
      >
        📅
      </button>
    </div>
  );
}