import { forwardRef, useId } from "react";
import styles from "./styles.module.css";

type Props = React.InputHTMLAttributes<HTMLInputElement> & {
  label?: string;
  helperText?: string;
  leftIcon?: React.ReactNode;
};

const Input = forwardRef<HTMLInputElement, Props>(function Input(
  { label, helperText, leftIcon, className, id, ...props },
  ref
) {
  const autoId = useId();
  const inputId = id ?? autoId;
  const helperId = helperText ? `${inputId}-help` : undefined;

  return (
    <div className={styles.wrap}>
      {label ? (
        <label className={styles.label} htmlFor={inputId}>
          {label}
        </label>
      ) : null}

      <div className={styles.control}>
        {leftIcon ? <span className={styles.icon} aria-hidden="true">{leftIcon}</span> : null}

        <input
          ref={ref}
          id={inputId}
          aria-describedby={helperId}
          className={[styles.input, leftIcon ? styles.withIcon : "", className ?? ""]
            .join(" ")
            .trim()}
          {...props}
        />
      </div>

      {helperText ? (
        <div className={styles.helper} id={helperId}>
          {helperText}
        </div>
      ) : null}
    </div>
  );
});

export default Input;