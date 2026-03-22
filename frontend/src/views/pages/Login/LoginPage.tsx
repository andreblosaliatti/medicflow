import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import BrandHeader from "../../../components/auth/BrandHeader";
import AuthCard from "../../../components/auth/AuthCard";
import AuthLinkDivider from "../../../components/auth/AuthLinkDivider";
import AuthPageLayout from "../../../components/auth/AuthPageLayout";
import TextField from "../../../components/auth/TextField";
import ErrorMessage from "../../../components/auth/ErrorMessage";
import PrimaryButton from "../../../components/ui/HighlightButton/HighlightButton";
import FooterMeta from "../../../components/auth/FooterMeta";
import { MailIcon, LockIcon } from "../../../components/auth/Icons";
import { useAuth } from "../../../auth/useAuth";

import "./styles.css";

type LoginLocationState = {
  from?: string;
};

export default function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { signIn, isAuthenticated } = useAuth();
  const [loginValue, setLoginValue] = useState("");
  const [senha, setSenha] = useState("");
  const [error, setError] = useState<string>("");
  const [isPending, setIsPending] = useState(false);

  const canSubmit = useMemo(() => {
    return loginValue.trim().length > 0 && senha.trim().length > 0;
  }, [loginValue, senha]);

  const redirectTo = ((location.state as LoginLocationState | null)?.from || "/dashboard");

  useEffect(() => {
    if (isAuthenticated) {
      navigate(redirectTo, { replace: true });
    }
  }, [isAuthenticated, navigate, redirectTo]);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();

    if (!loginValue.trim()) {
      setError("Informe seu login.");
      return;
    }

    if (senha.length < 4) {
      setError("Senha inválida.");
      return;
    }

    setError("");
    setIsPending(true);

    try {
      await signIn({ login: loginValue.trim(), senha });
      navigate(redirectTo, { replace: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : "Não foi possível autenticar.");
    } finally {
      setIsPending(false);
    }
  }

  return (
    <AuthPageLayout>
      <BrandHeader />

      <AuthCard title="Login">
        <form onSubmit={onSubmit} className="auth-form" noValidate>
          <TextField
            placeholder="Login"
            value={loginValue}
            onChange={(v) => setLoginValue(v)}
            type="text"
            autoComplete="username"
            leftIcon={<MailIcon />}
            ariaLabel="Login"
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

          <ErrorMessage message={error} />

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
