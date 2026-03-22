import { useAuth } from "../../../auth/useAuth";
import DoctorDashboard from "./DoctorDashboard";

export default function DashboardPage() {
  const { user } = useAuth();

  if (user?.role === "MEDICO") return <DoctorDashboard />;

  return <DoctorDashboard />;
}
