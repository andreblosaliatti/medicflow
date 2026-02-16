import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";

import PageHeader from "../../../../components/layout/PageHeader/PageHeader";
import PacienteForm from "./PacientForm";

import { emptyPaciente, type PacienteDTO } from "../../../../mocks/db/seed";
import { getNextPacienteId, getPacienteById, savePaciente } from "./pacientStore";

type NavState = { from?: string };

export default function PacienteFormPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const params = useParams();

  const id = useMemo(() => {
    const raw = params.id;
    if (!raw) return null;
    const n = Number(raw);
    return Number.isFinite(n) ? n : null;
  }, [params.id]);

  const mode: "create" | "edit" = id !== null ? "edit" : "create";

  const routeModel = useMemo<PacienteDTO>(() => {
    if (id !== null) {
      const found = getPacienteById(id);
      return found ?? emptyPaciente(getNextPacienteId());
    }
    return emptyPaciente(getNextPacienteId());
  }, [id]);

  const [model, setModel] = useState<PacienteDTO>(routeModel);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    setModel(routeModel);

    if (id !== null) {
      const exists = getPacienteById(id);
      if (!exists) navigate("/pacientes", { replace: true });
    }
  }, [routeModel, id, navigate]);

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

  function handleSubmit() {
    try {
      setSubmitting(true);

      const saved = savePaciente(model);

      if (mode === "create") {
        navigate(`/pacientes/${saved.id}/editar`, { replace: true });
        return;
      }

      const state = location.state as NavState | null;
      const from = state?.from;
      if (from) {
        navigate(from, { replace: true });
      } else {
        navigate("/pacientes", { replace: true });
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <>
      <PageHeader title={mode === "create" ? "Novo paciente" : "Editar paciente"} />

      <PacienteForm
        value={model}
        onChange={setModel}
        onSubmit={handleSubmit}
        submitting={submitting}
        mode={mode}
        onCancel={handleCancel}
      />
    </>
  );
}
