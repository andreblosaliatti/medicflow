import { BrowserRouter, Route, Routes, Navigate } from "react-router-dom";

import LoginPage from "./views/pages/Login/LoginPage";
import DashboardPage from "./views/pages/Dashboard/DashboardPage";
import AgendaPage from "./views/pages/Agenda/AgendaPage";

import AppLayout from "./components/layout/AppLayout";

// Pacientes
import PacientesPage from "./views/pages/Pacientes/Pacients";
import PacientProfile from "./views/pages/Pacientes/PacientProfile/PacientProfile";
import ProntuarioPage from "./views/pages/Pacientes/Prontuario/Prontuario";
import PacienteFormPage from "./views/pages/Pacientes/PacientForm/PacientFormPage";

// Consultas
import ConsultasPage from "./views/pages/Consultas/Consulta";
import ConsultaDetails from "./views/pages/Consultas/ConsultaDetails";
import ConsultaEdit from "./views/pages/Consultas/ConsultaEdit";
import ConsultaNew from "./views/pages/Consultas/ConsultaNew";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Login */}
        <Route path="/" element={<LoginPage />} />

        {/* Layout protegido */}
        <Route element={<AppLayout />}>

          {/* Dashboard */}
          <Route path="/dashboard" element={<DashboardPage />} />

          {/* Agenda */}
          <Route path="/agenda" element={<AgendaPage />} />

          {/* Pacientes */}
          <Route path="/pacientes">
            <Route index element={<PacientesPage />} />
            <Route path="novo" element={<PacienteFormPage />} />
            <Route path=":id/editar" element={<PacienteFormPage />} />
            <Route path=":id/prontuario" element={<ProntuarioPage />} />
            <Route path=":id" element={<PacientProfile />} />
          </Route>

          {/* Consultas */}
          <Route path="/consultas">
            <Route index element={<ConsultasPage />} />
            <Route path="nova" element={<ConsultaNew />} />
            <Route path=":id/editar" element={<ConsultaEdit />} />
            <Route path=":id" element={<ConsultaDetails />} />
          </Route>

        </Route>

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}