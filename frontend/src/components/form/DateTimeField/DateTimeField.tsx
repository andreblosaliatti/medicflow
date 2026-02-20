import { useEffect, useId, useRef, useState } from "react";
import "./styles.css";

type Props = {
  value: string;
  onChange: (value: string) => void;

  id?: string;
  name?: string;
  placeholder?: string;
  disabled?: boolean;

  min?: string;
  max?: string;
  step?: number;
  "aria-label"?: string;
};

export default function DateTimeField({
  value,
  onChange,
  id,
  name,
  placeholder,
  disabled,
  min,
  max,
  step,
  "aria-label": ariaLabel,
}: Props) {
  const autoId = useId();
  const inputId = id ?? `dt-${autoId}`;

  const inputRef = useRef<HTMLInputElement | null>(null);
  const [pickerOpen, setPickerOpen] = useState(false);

  // Heurística: foco costuma significar picker aberto (ou prestes a abrir)
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
      // tentativa de "fechar" o nativo: blur
      el.blur();
      setPickerOpen(false);
      return;
    }

    // abrir
    el.focus();
    // Chrome/Edge suportam showPicker()

    if (typeof el.showPicker === "function") el.showPicker();
    setPickerOpen(true);
  }

  return (
    <div className="mf-dtWrap">
      <input
        ref={inputRef}
        id={inputId}
        name={name}
        className="mf-dtInput"
        type="datetime-local"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        disabled={disabled}
        min={min}
        max={max}
        step={step}
        aria-label={ariaLabel}
      />

      {/* botão clicável (toggle) */}
      <button
        type="button"
        className="mf-dtBtn"
        onMouseDown={(e) => e.preventDefault()} // evita tirar foco do input antes do toggle
        onClick={togglePicker}
        aria-label={pickerOpen ? "Fechar calendário" : "Abrir calendário"}
        disabled={disabled}
      >
        📅
      </button>
    </div>
  );
}