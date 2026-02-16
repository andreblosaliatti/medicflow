import { getSessionUser } from "../../../auth/session";
import DoctorDashboard from "./DoctorDashboard";
// no futuro:
// import AdminDashboard from "./AdminDashboard";
// import SecretariaDashboard from "./SecretariaDashboard";

export default function DashboardPage() {
  const user = getSessionUser();

  if (user.role === "MEDICO") return <DoctorDashboard />;

  // futuro:
  // if (user.role === "ADMIN") return <AdminDashboard />;
  // if (user.role === "SECRETARIA") return <SecretariaDashboard />;

  return <DoctorDashboard />; // fallback por enquanto
}
