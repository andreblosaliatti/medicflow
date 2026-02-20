import { useState } from "react";
import { useNavigate } from "react-router-dom";

import AppPage from "../../../components/layout/AppPage/AppPage";
import PageHeader from "../../../components/layout/PageHeader/PageHeader";
import Card from "../../../components/ui/Card";
import PrimaryButton from "../../../components/ui/PrimaryButton/PrimaryButton";
import ConsultaFormFields from "../../../components/consulta/ConsultaFormFields";

import { MEDICO_SEED } from "../../../mocks/db/seed";
import type { ConsultaDTO } from "../../../mocks/db/seed";

import "./styles.css";

function createLocalId() {
  const n = Date.now().toString(36);
  return `c-${n}`;
}

export default function ConsultaNew() {
  const navigate = useNavigate();

  const [form, setForm] = useState<ConsultaDTO>(() => ({
    id: createLocalId(),
    pacienteId: 1,
    medicoId: MEDICO_SEED.id,
    medicoNome: MEDICO_SEED.nome,
    dataHora: "2026-02-20T09:00",
    duracaoMinutos: 30,
    tipo: "PRESENCIAL",
    status: "AGENDADA",
    motivo: "",
    sala: "Sala 01",
    telefoneContato: "",
    valorConsulta: 0,
    pago: false,
    meioPagamento: "PIX",
  }));

  return (
    <AppPage
      header={
        <PageHeader
          title="Nova consulta"
          subtitle="Criar agendamento"
          actionLabel="Salvar"
          onAction={() => {
            console.log("Salvar (mock):", form);
            navigate("/consultas");
          }}
        />
      }
    >
      <Card>
        <div className="consultas-form">
          <ConsultaFormFields form={form} onChange={setForm} />

          <div className="consultas-actionsBottom">
            <PrimaryButton onClick={() => navigate("/consultas")}>Cancelar</PrimaryButton>
            <PrimaryButton
              onClick={() => {
                console.log("Salvar (mock):", form);
                navigate("/consultas");
              }}
            >
              Salvar
            </PrimaryButton>
          </div>
        </div>
      </Card>
    </AppPage>
  );
}
