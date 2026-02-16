import { BrowserRouter, Route, Routes, Navigate } from "react-router-dom";
import LoginPage from "./views/pages/Login/LoginPage";
import DashboardPage from "./views/pages/Dashboard/DashboardPage";
import AgendaPage from "./views/pages/Agenda/AgendaPage";
import AppLayout from "./components/layout/AppLayout";
import PacientesPage from "./views/pages/Pacientes/Pacients";
import PacientProfile from "./views/pages/Pacientes/PacientProfile/PacientProfile"
import ProntuarioPage from "./views/pages/Pacientes/Prontuario/Prontuario";
import PacienteFormPage from "./views/pages/Pacientes/PacientForm/PacientFormPage";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route element={<AppLayout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/agenda" element={<AgendaPage />} />
          <Route path="/pacientes">
            <Route index element={<PacientesPage />} />
            <Route path="novo" element={<PacienteFormPage />} />
            <Route path=":id/editar" element={<PacienteFormPage />} />
            <Route path=":id/prontuario" element={<ProntuarioPage />} />
            <Route path=":id" element={<PacientProfile />} />
          </Route>
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}