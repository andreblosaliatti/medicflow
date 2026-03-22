// src/pages/Login/LoginPage.tsx
import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import BrandHeader from "../../../components/auth/BrandHeader";
import AuthCard from "../../../components/auth/AuthCard";
import AuthLinkDivider from "../../../components/auth/AuthLinkDivider";
import AuthPageLayout from "../../../components/auth/AuthPageLayout";
import TextField from "../../../components/auth/TextField";
import ErrorMessage from "../../../components/auth/ErrorMessage";
import PrimaryButton from "../../../components/ui/HighlightButton/HighlightButton";
import FooterMeta from "../../../components/auth/FooterMeta";
import { MailIcon, LockIcon } from "../../../components/auth/Icons";
import { useLoginMutation } from "../../../api/auth/hooks";

import "./styles.css";

export default function LoginPage() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [error, setError] = useState<string>("");
  const { mutateAsync: login, isPending, error: apiError } = useLoginMutation();

  const canSubmit = useMemo(() => {
    return email.trim().length > 0 && senha.trim().length > 0;
  }, [email, senha]);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();

    if (!email.includes("@")) {
      setError("E-mail inválido");
      return;
    }
    if (senha.length < 4) {
      setError("Senha inválida");
      return;
    }

    setError("");
    const result = await login({ email, senha });

    if (result) {
      navigate("/dashboard");
    }
  }

  return (
    <AuthPageLayout>
      <BrandHeader />

      <AuthCard title="Login">
        <form onSubmit={onSubmit} className="auth-form" noValidate>
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

          <ErrorMessage message={error || apiError || ""} />

          <PrimaryButton type="submit" disabled={!canSubmit || isPending}>
            {isPending ? "Entrando..." : "Entrar"}
          </PrimaryButton>

          <AuthLinkDivider to="/esqueci-senha">Esqueci minha senha</AuthLinkDivider>

          <FooterMeta version="v1.0.0" />
        </form>
      </AuthCard>
    </AuthPageLayout>
  );
}
