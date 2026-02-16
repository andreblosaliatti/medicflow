import { forwardRef } from "react";
import styles from "./styles.module.css";

type Props = React.InputHTMLAttributes<HTMLInputElement> & {
  label?: string;
  helperText?: string;
  leftIcon?: React.ReactNode;
};

const Input = forwardRef<HTMLInputElement, Props>(function Input(
  { label, helperText, leftIcon, className, ...props },
  ref
) {
  return (
    <div className={styles.wrap}>
      {label && <label className={styles.label}>{label}</label>}

      <div className={styles.control}>
        {leftIcon && <span className={styles.icon}>{leftIcon}</span>}

        <input
          ref={ref}
          className={[styles.input, leftIcon ? styles.withIcon : "", className || ""].join(" ").trim()}
          {...props}
        />
      </div>

      {helperText && <div className={styles.helper}>{helperText}</div>}
    </div>
  );
});

export default Input;
