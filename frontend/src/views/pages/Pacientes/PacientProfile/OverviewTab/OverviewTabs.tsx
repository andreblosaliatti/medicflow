import type { PacienteProfileViewModel } from "../../../../../api/pacientes/types";
import Card from "../../../../../components/ui/Card";

export default function OverviewTab({ patient }: { patient: PacienteProfileViewModel }) {
  return (
    <div className="patient-overview-grid">
      <div className="patient-overview-card">
        <Card>
          <h3>Dados Pessoais</h3>
          <p>CPF: {patient.cpf}</p>
          <p>Data de Nascimento: {patient.dataNascimentoLabel}</p>
          <p>Sexo: {patient.sexo}</p>
          <p>Convênio: {patient.planoSaude}</p>
        </Card>
      </div>

      <div className="patient-overview-card">
        <Card>
          <h3>Contato e Endereço</h3>
          <p>Telefone: {patient.telefone}</p>
          <p>E-mail: {patient.email}</p>
          <p>Endereço: {patient.enderecoCompleto}</p>
        </Card>
      </div>

      <div className="patient-overview-card">
        <Card>
          <h3>Resumo clínico</h3>
          <p>Total de consultas: {patient.historico.totalConsultas}</p>
          <p>Exames solicitados: {patient.historico.totalExamesSolicitados}</p>
          <p>Medicamentos prescritos: {patient.historico.totalMedicamentosPrescritos}</p>
          <p>Última consulta: {patient.historico.dataUltimaConsultaLabel}</p>
        </Card>
      </div>

      <div className="patient-overview-card">
        <Card>
          <h3>Última consulta</h3>
          {patient.historico.ultimaConsulta ? (
            <>
              <p>Data: {patient.historico.ultimaConsulta.dataLabel}</p>
              <p>Hora: {patient.historico.ultimaConsulta.horaLabel}</p>
              <p>Profissional: {patient.historico.ultimaConsulta.medicoNome}</p>
              <p>Status: {patient.historico.ultimaConsulta.statusLabel}</p>
              <p>Tipo: {patient.historico.ultimaConsulta.tipoLabel}</p>
              <p>Motivo: {patient.historico.ultimaConsulta.motivo}</p>
            </>
          ) : (
            <p>Nenhuma consulta encontrada.</p>
          )}
        </Card>
      </div>
    </div>
  );
}
