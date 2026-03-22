import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";

import { AuthProvider } from "./context/AuthContext";
import { useAuth } from "./context/useAuth";
import PrivateRoute from "./router/PrivateRoute";
import AppLayout from "./components/layout/AppLayout";
import AgendaPage from "./views/pages/Agenda/AgendaPage";
import DashboardPage from "./views/pages/Dashboard/DashboardPage";
import LoginPage from "./pages/LoginPage";
import ForgotPasswordPage from "./views/pages/ForgotPassword/ForgotPasswordPage";
import PendenciasPage from "./views/pages/Pendencias";
import ConsultaAtendimento from "./views/pages/Consultas/ConsultaAtendimento/ConsultaAtendimento";
import ConsultaDetails from "./views/pages/Consultas/ConsultaDetail/ConsultaDetails";
import ConsultaEdit from "./views/pages/Consultas/ConsultaEdit/ConsultaEdit";
import ConsultaNew from "./views/pages/Consultas/ConsultaEdit/ConsultaNew";
import ConsultasPage from "./views/pages/Consultas/Consultas/Consulta";
import PacienteFormPage from "./views/pages/Pacientes/PacientForm/PacientFormPage";
import PacientProfile from "./views/pages/Pacientes/PacientProfile/PacientProfile";
import PacientesPage from "./views/pages/Pacientes/Pacients";
import PrescricoesPage from "./views/pages/Pacientes/Prescricoes";
import ProntuarioPage from "./views/pages/Pacientes/Prontuario/Prontuario";

function RootRedirect() {
  const { isLoading, isAuthenticated } = useAuth();

  if (isLoading) {
    return <div className="mf-page-loading">Carregando sessão...</div>;
  }

  return <Navigate to={isAuthenticated ? "/dashboard" : "/login"} replace />;
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/" element={<RootRedirect />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/esqueci-senha" element={<ForgotPasswordPage />} />

          <Route element={<PrivateRoute />}>
            <Route element={<AppLayout />}>
              <Route path="/dashboard" element={<DashboardPage />} />
              <Route path="/agenda" element={<AgendaPage />} />

              <Route path="/pacientes">
                <Route index element={<PacientesPage />} />
                <Route path="novo" element={<PacienteFormPage />} />
                <Route path=":id/editar" element={<PacienteFormPage />} />
                <Route path=":id/prescricoes" element={<PrescricoesPage />} />
                <Route path=":id/prontuario" element={<ProntuarioPage />} />
                <Route path=":id" element={<PacientProfile />} />
              </Route>

              <Route element={<PrivateRoute allowedRoles={["MEDICO"]} />}>
                <Route path="/consultas">
                  <Route index element={<ConsultasPage />} />
                  <Route path="nova" element={<ConsultaNew />} />
                  <Route path=":id/editar" element={<ConsultaEdit />} />
                  <Route path=":id/atendimento" element={<ConsultaAtendimento />} />
                  <Route path=":id" element={<ConsultaDetails />} />
                </Route>

                <Route path="/pendencias" element={<PendenciasPage />} />
              </Route>
            </Route>
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
