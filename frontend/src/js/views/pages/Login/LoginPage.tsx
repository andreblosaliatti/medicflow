// src/pages/Login/LoginPage.tsx
import { useMemo, useState } from "react";
import BrandHeader from "../../components/auth/BrandHeader";
import AuthCard from "../../components/auth/AuthCard";
import TextField from "../../components/auth/TextField";
import ErrorMessage from "../../components/auth/ErrorMessage";
import PrimaryButton from "../../components/auth/PrimaryButton";
import FooterMeta from "../../components/auth/FooterMeta";
import { MailIcon, LockIcon } from "../../components/auth/icons";

import "./styles.css";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [error, setError] = useState<string>("");

  const canSubmit = useMemo(() => {
    return email.trim().length > 0 && senha.trim().length > 0;
  }, [email, senha]);

  function onSubmit(e: React.FormEvent) {
    e.preventDefault();

    // Validação simples (troque depois pela chamada da API)
    if (!email.includes("@")) {
      setError("E-mail inválido");
      return;
    }
    if (senha.length < 4) {
      setError("Senha inválida");
      return;
    }

    setError("");

    // TODO: integrar com sua API (Spring Boot) e tratar retorno/erro
    console.log("Login:", { email, senha });
  }

  return (
    <div className="login-page">
      <BrandHeader />

      <AuthCard title="Login">
        <form onSubmit={onSubmit} className="login-form" noValidate>
          <TextField
            placeholder="E-mail"
            value={email}
            onChange={(v) => setEmail(v)}
            type="email"
            autoComplete="email"
            leftIcon={<MailIcon />}
            ariaLabel="E-mail"
          />

          <TextField
            placeholder="Senha"
            value={senha}
            onChange={(v) => setSenha(v)}
            type="password"
            autoComplete="current-password"
            leftIcon={<LockIcon />}
            ariaLabel="Senha"
          />

          <ErrorMessage message={error || ""} />

          <PrimaryButton type="submit" disabled={!canSubmit}>
            Entrar
          </PrimaryButton>

          <div className="forgot-wrap">
            <span className="line" />
            <a className="forgot-link" href="/esqueci-senha">
              Esqueci minha senha
            </a>
            <span className="line" />
          </div>

          <FooterMeta version="v1.0.0" />
        </form>
      </AuthCard>
    </div>
  );
}
