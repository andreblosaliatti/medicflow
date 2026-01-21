// src/components/auth/PrimaryButton.tsx
import "./auth.css";

type Props = React.ButtonHTMLAttributes<HTMLButtonElement>;

export default function PrimaryButton({ children, ...rest }: Props) {
  return (
    <button className="primary-btn" {...rest}>
      {children}
    </button>
  );
}
