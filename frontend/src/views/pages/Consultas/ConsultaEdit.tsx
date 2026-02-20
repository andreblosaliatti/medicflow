import { useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import AppPage from "../../../components/layout/AppPage/AppPage";
import PageHeader from "../../../components/layout/PageHeader/PageHeader";
import Card from "../../../components/ui/Card";
import PrimaryButton from "../../../components/ui/PrimaryButton/PrimaryButton";
import ConsultaFormFields from "../../../components/consulta/ConsultaFormFields";

import type { ConsultaDTO } from "../../../mocks/db/seed";
import { getConsultaById } from "../../../mocks/mappers";

import "./styles.css";

export default function ConsultaEdit() {
  const navigate = useNavigate();
  const { id } = useParams();

  const initial = useMemo(() => (id ? getConsultaById(id) : null), [id]);
  const [form, setForm] = useState<ConsultaDTO | null>(initial);

  if (!form) {
    return (
      <AppPage header={<PageHeader title="Editar consulta" subtitle="Não encontrada" />}>
        <></>
      </AppPage>
    );
  }

  return (
    <AppPage
      header={
        <PageHeader
          title="Editar consulta"
          subtitle={form.id}
          actionLabel="Salvar"
          onAction={() => {
            console.log("Salvar edição (mock):", form);
            navigate(`/consultas/${form.id}`);
          }}
        />
      }
    >
      <Card>
        <div className="consultas-form">
          <ConsultaFormFields form={form} onChange={(next) => setForm(next)} />

          <div className="consultas-actionsBottom">
            <PrimaryButton onClick={() => navigate(`/consultas/${form.id}`)}>Cancelar</PrimaryButton>

            <PrimaryButton
              onClick={() => {
                console.log("Salvar edição (mock):", form);
                navigate(`/consultas/${form.id}`);
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
