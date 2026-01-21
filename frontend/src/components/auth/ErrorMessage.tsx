// src/components/auth/ErrorMessage.tsx
import { WarningIcon } from "./Icons";
import "./auth.css";

type Props = {
  message: string;
};

export default function ErrorMessage({ message }: Props) {
  if (!message) return <div className="error-placeholder" />;

  return (
    <div className="error-row" role="alert" aria-live="polite">
      <span className="error-icon" aria-hidden="true">
        <WarningIcon />
      </span>
      <span className="error-text">{message}</span>
    </div>
  );
}
