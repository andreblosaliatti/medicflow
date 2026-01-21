// src/components/auth/AuthCard.tsx
import "./auth.css";

type Props = {
  title: string;
  children: React.ReactNode;
};

export default function AuthCard({ title, children }: Props) {
  return (
    <section className="auth-card" role="region" aria-label={title}>
      <h1 className="auth-title">{title}</h1>
      <div className="auth-content">{children}</div>
    </section>
  );
}
