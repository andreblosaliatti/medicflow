import { useNavigate } from "react-router-dom";

import type { PacienteProfileViewModel } from "../../../api/pacientes/types";
import Card from "../../ui/Card";
import "./styles.css";
import HighlightButton from "../../ui/HighlightButton/HighlightButton";

export default function PatientHeaderCard({ patient }: { patient: PacienteProfileViewModel }) {
  const navigate = useNavigate();

  function handleNovaConsulta() {
    navigate("/agenda", {
      state: {
        novaConsulta: {
          pacienteId: String(patient.id),
          pacienteNome: patient.nomeCompleto,
        },
      },
    });
  }

  function handleIrParaProntuario() {
    navigate(`/pacientes/${patient.id}?tab=prontuario`);
  }

  return (
    <Card>
      <div className="patient-header">
        <div className="patient-header-left">
          <div className="patient-avatar" aria-hidden="true">
            {patient.initials}
          </div>

          <div>
            <h2>
              {patient.nomeCompleto} ({patient.idadeLabel})
            </h2>
            <span className="patient-subtitle">
              Paciente • {patient.ativo ? "Ativo" : "Inativo"}
            </span>
          </div>
        </div>

        <div className="patient-header-actions">
          <HighlightButton onClick={handleNovaConsulta}>+ Nova Consulta</HighlightButton>

          <HighlightButton onClick={handleIrParaProntuario}>Ir para Prontuário</HighlightButton>
        </div>
      </div>
    </Card>
  );
}
