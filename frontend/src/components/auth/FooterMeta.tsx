// src/components/auth/FooterMeta.tsx
import "./auth.css";

type Props = {
  version: string;
};

export default function FooterMeta({ version }: Props) {
  return (
    <div className="footer-meta" aria-label="Versão do sistema">
      <span className="footer-line" />
      <span className="footer-version">{version}</span>
      <span className="footer-line" />
    </div>
  );
}
