import { useNavigate, useParams } from "react-router-dom";

import Card from "../../ui/Card";
import "./styles.css";
import HighlightButton from "../../ui/HighlightButton/HighlightButton";

export default function PatientHeaderCard() {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();

  function handleNovaConsulta() {
    navigate(`/agenda/novo?pacienteId=${id}`);
  }

  function handleIrParaProntuario() {
    navigate(`/pacientes/${id}?tab=prontuario`);
  }

  return (
    <Card>
      <div className="patient-header">
        <div className="patient-header-left">
          <img
            src="https://i.pravatar.cc/100"
            alt="Paciente"
            className="patient-avatar"
          />

          <div>
            <h2>Sofia Almeida (35 anos)</h2>
            <span className="patient-subtitle">Paciente</span>
          </div>
        </div>

        <div className="patient-header-actions">
          <HighlightButton onClick={handleNovaConsulta}>
            + Nova Consulta
          </HighlightButton>

          <HighlightButton
            onClick={handleIrParaProntuario}
          >
            Ir para Prontuário
          </HighlightButton>
        </div>
      </div>
    </Card>
  );
}