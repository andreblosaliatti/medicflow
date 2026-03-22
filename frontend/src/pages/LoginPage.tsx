import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

import BrandHeader from "../components/auth/BrandHeader";
import AuthCard from "../components/auth/AuthCard";
import AuthLinkDivider from "../components/auth/AuthLinkDivider";
import AuthPageLayout from "../components/auth/AuthPageLayout";
import TextField from "../components/auth/TextField";
import ErrorMessage from "../components/auth/ErrorMessage";
import PrimaryButton from "../components/ui/HighlightButton/HighlightButton";
import FooterMeta from "../components/auth/FooterMeta";
import { MailIcon, LockIcon } from "../components/auth/Icons";
import { consumePostLoginRedirect } from "../auth/session";
import { useAuth } from "../context/useAuth";

import "../views/pages/Login/styles.css";

type LoginLocationState = {
  from?: string;
};

export default function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, isAuthenticated, isLoading } = useAuth();

  const [loginValue, setLoginValue] = useState("");
  const [senha, setSenha] = useState("");
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const redirectTo = useMemo(() => {
    const stateRedirect = (location.state as LoginLocationState | null)?.from;
    return stateRedirect || consumePostLoginRedirect() || "/dashboard";
  }, [location.state]);

  useEffect(() => {
    if (!isLoading && isAuthenticated) {
      navigate(redirectTo, { replace: true });
    }
  }, [isAuthenticated, isLoading, navigate, redirectTo]);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const submittedLogin = loginValue.trim();
    const submittedSenha = senha;

    if (!submittedLogin) {
      setError("Informe seu login.");
      return;
    }

    if (!submittedSenha) {
      setError("Informe sua senha.");
      return;
    }

    setError("");
    setIsSubmitting(true);

    try {
      await login({ login: submittedLogin, senha: submittedSenha });
      navigate(redirectTo, { replace: true });
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : "Não foi possível autenticar.");
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <AuthPageLayout>
      <BrandHeader />

      <AuthCard title="Login">
        <form onSubmit={handleSubmit} className="auth-form" noValidate>
          <TextField
            name="login"
            placeholder="Login"
            value={loginValue}
            onChange={setLoginValue}
            type="text"
            autoComplete="username"
            leftIcon={<MailIcon />}
            ariaLabel="Login"
          />

          <TextField
            name="senha"
            placeholder="Senha"
            value={senha}
            onChange={setSenha}
            type="password"
            autoComplete="current-password"
            leftIcon={<LockIcon />}
            ariaLabel="Senha"
          />

          <ErrorMessage message={error} />

          <PrimaryButton type="submit" disabled={isSubmitting || isLoading}>
            {isSubmitting ? "Entrando..." : "Entrar"}
          </PrimaryButton>

          <AuthLinkDivider to="/esqueci-senha">Esqueci minha senha</AuthLinkDivider>

          <FooterMeta version="v1.0.0" />
        </form>
      </AuthCard>
    </AuthPageLayout>
  );
}
