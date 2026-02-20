import { useMemo, useState } from "react";

import BrandHeader from "../../../components/auth/BrandHeader";
import AuthCard from "../../../components/auth/AuthCard";
import AuthLinkDivider from "../../../components/auth/AuthLinkDivider";
import AuthPageLayout from "../../../components/auth/AuthPageLayout";
import TextField from "../../../components/auth/TextField";
import ErrorMessage from "../../../components/auth/ErrorMessage";
import PrimaryButton from "../../../components/ui/HighlightButton/HighlightButton";
import FooterMeta from "../../../components/auth/FooterMeta";
import { MailIcon } from "../../../components/auth/Icons";

import "./styles.css";

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState("");
  const [error, setError] = useState<string>("");
  const [success, setSuccess] = useState<string>("");

  const canSubmit = useMemo(() => email.trim().length > 0, [email]);

  function onSubmit(e: React.FormEvent) {
    e.preventDefault();

    setError("");
    setSuccess("");

    if (!email.includes("@")) {
      setError("Informe um e-mail válido.");
      return;
    }

    // TODO: chamar endpoint real: POST /auth/forgot-password (exemplo)
    setSuccess("Se este e-mail estiver cadastrado, enviaremos um link de recuperação.");
  }

  return (
    <AuthPageLayout pageClassName="forgot-page">
      <BrandHeader />

      <AuthCard title="Esqueci minha senha">
        <form onSubmit={onSubmit} className="auth-form forgot-form" noValidate>
          <p className="forgot-help">
            Informe seu e-mail para receber um link de recuperação.
          </p>

          <TextField
            placeholder="E-mail"
            value={email}
            onChange={setEmail}
            type="email"
            autoComplete="email"
            leftIcon={<MailIcon />}
            ariaLabel="E-mail"
          />

          <ErrorMessage message={error} />

          {success ? <div className="success-row">{success}</div> : <div className="success-placeholder" />}

          <PrimaryButton type="submit" disabled={!canSubmit}>
            Enviar link
          </PrimaryButton>

          <AuthLinkDivider to="/login">Voltar para o login</AuthLinkDivider>

          <FooterMeta version="v1.0.0" />
        </form>
      </AuthCard>
    </AuthPageLayout>
  );
}
