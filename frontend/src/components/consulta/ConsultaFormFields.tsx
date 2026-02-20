import Input from "../form/Input";
import { seedPacientes } from "../../mocks/db/seed";
import type { ConsultaDTO } from "../../mocks/db/seed";
import type { DuracaoMinutos, TipoConsulta } from "../../domain/enums/statusConsulta";

type ConsultaFormFieldsProps = {
  form: ConsultaDTO;
  onChange: (next: ConsultaDTO) => void;
};

const DURACOES: DuracaoMinutos[] = [15, 30, 45, 60];
const TIPOS: TipoConsulta[] = ["PRESENCIAL", "TELECONSULTA", "RETORNO"];

function isDuracaoMinutos(v: number): v is DuracaoMinutos {
  return (DURACOES as number[]).includes(v);
}

function isTipoConsulta(v: string): v is TipoConsulta {
  return (TIPOS as string[]).includes(v);
}

export default function ConsultaFormFields({ form, onChange }: ConsultaFormFieldsProps) {
  return (
    <>
      <label className="consultas-field">
        <span className="consultas-label">Paciente</span>
        <select
          className="consultas-select"
          value={form.pacienteId}
          onChange={(e) => {
            const pacienteId = Number(e.target.value);
            onChange({ ...form, pacienteId });
          }}
        >
          {seedPacientes.map((p) => (
            <option key={p.id} value={p.id}>
              {p.primeiroNome} {p.sobrenome}
            </option>
          ))}
        </select>
      </label>

      <label className="consultas-field">
        <span className="consultas-label">Médico</span>
        <Input value={form.medicoNome} disabled />
      </label>

      <label className="consultas-field">
        <span className="consultas-label">Data/Hora</span>
        <Input
          value={form.dataHora}
          onChange={(e) => onChange({ ...form, dataHora: e.target.value })}
          placeholder="yyyy-mm-ddTHH:mm"
        />
      </label>

      <div className="consultas-row2">
        <label className="consultas-field">
          <span className="consultas-label">Duração</span>
          <select
            className="consultas-select"
            value={form.duracaoMinutos}
            onChange={(e) => {
              const n = Number(e.target.value);
              if (!isDuracaoMinutos(n)) return;
              onChange({ ...form, duracaoMinutos: n });
            }}
          >
            {DURACOES.map((d) => (
              <option key={d} value={d}>
                {d} min
              </option>
            ))}
          </select>
        </label>

        <label className="consultas-field">
          <span className="consultas-label">Tipo</span>
          <select
            className="consultas-select"
            value={form.tipo}
            onChange={(e) => {
              const v = e.target.value;
              if (!isTipoConsulta(v)) return;
              onChange({ ...form, tipo: v });
            }}
          >
            {TIPOS.map((t) => (
              <option key={t} value={t}>
                {t}
              </option>
            ))}
          </select>
        </label>
      </div>

      <label className="consultas-field">
        <span className="consultas-label">Motivo</span>
        <textarea
          className="consultas-textarea"
          value={form.motivo ?? ""}
          onChange={(e) => onChange({ ...form, motivo: e.target.value })}
          placeholder="Descreva a queixa principal..."
        />
      </label>
    </>
  );
}
