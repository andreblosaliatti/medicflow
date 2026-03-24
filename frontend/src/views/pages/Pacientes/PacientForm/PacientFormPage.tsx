import { useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";

import PageHeader from "../../../../components/layout/PageHeader/PageHeader";
import PacienteForm from "./PacientForm";

import { emptyPacienteForm, toPacienteFormValues } from "../../../../api/pacientes/mappers";
import { useCreatePacienteMutation, usePacienteByIdQuery, useUpdatePacienteMutation } from "../../../../api/pacientes/hooks";
import type { PacienteFormValues } from "../../../../api/pacientes/types";

type NavState = { from?: string };

type FormMode = "create" | "edit";

function PacienteFormScreen({
  mode,
  id,
  initialModel,
}: {
  mode: FormMode;
  id: number | null;
  initialModel: PacienteFormValues;
}) {
  const navigate = useNavigate();
  const location = useLocation();
  const [model, setModel] = useState<PacienteFormValues>(initialModel);

  const createMutation = useCreatePacienteMutation();
  const updateMutation = useUpdatePacienteMutation();

  function handleCancel() {
    const state = location.state as NavState | null;
    const current = `${location.pathname}${location.search}`;
    const from = state?.from;

    if (!from || from === current) {
      navigate("/pacientes", { replace: true });
      return;
    }

    navigate(from, { replace: true });
  }

  async function handleSubmit() {
    if (mode === "create") {
      const created = await createMutation.mutateAsync(model);
      if (!created) return;
      navigate(`/pacientes/${created.id}`, { replace: true });
      return;
    }

    if (id === null) return;

    const updated = await updateMutation.mutateAsync({ id, values: model });
    if (!updated) return;

    const state = location.state as NavState | null;
    const from = state?.from;

    if (from) {
      navigate(from, { replace: true });
      return;
    }

    navigate(`/pacientes/${id}`, { replace: true });
  }

  const error = createMutation.error ?? updateMutation.error;
  const submitting = createMutation.isPending || updateMutation.isPending;

  return (
    <>
      {error ? <div className="mf-page-content mf-muted">{error}</div> : null}
      <PacienteForm
        value={model}
        onChange={setModel}
        onSubmit={() => void handleSubmit()}
        submitting={submitting}
        mode={mode}
        onCancel={handleCancel}
      />
    </>
  );
}

export default function PacienteFormPage() {
  const params = useParams();

  const id = params.id ? Number(params.id) : null;
  const pacienteId = Number.isFinite(id) ? id : null;
  const mode: FormMode = pacienteId !== null ? "edit" : "create";
  const pacienteQuery = usePacienteByIdQuery(pacienteId);

  return (
    <>
      <PageHeader title={mode === "create" ? "Novo paciente" : "Editar paciente"} />

      {mode === "create" ? <PacienteFormScreen key="create" mode="create" id={null} initialModel={emptyPacienteForm()} /> : null}

      {mode === "edit" && pacienteQuery.isLoading && !pacienteQuery.data ? (
        <div className="mf-page-content">Carregando paciente...</div>
      ) : null}

      {mode === "edit" && pacienteQuery.error ? <div className="mf-page-content mf-muted">{pacienteQuery.error}</div> : null}

      {mode === "edit" && pacienteQuery.data ? (
        <PacienteFormScreen
          key={`edit-${pacienteQuery.data.id}`}
          mode="edit"
          id={pacienteQuery.data.id}
          initialModel={toPacienteFormValues(pacienteQuery.data)}
        />
      ) : null}
    </>
  );
}
