import { useMemo, useState } from "react";

import Card from "../../../../components/ui/Card";
import Input from "../../../../components/form/Input";
import SelectField, { type SelectOption } from "../../../../components/form/SelectField/SelectField";
import HighlightButton from "../../../../components/ui/HighlightButton/HighlightButton";
import DateField from "../../../../components/form/DateField/DateField";

import type { PacienteFormValues, PacienteSexo } from "../../../../api/pacientes/types";
import { formatCPF, formatPhoneBR, type FormErrors, validatePaciente } from "./formUtils";

import "./styles.css";

export default function PacienteForm({
  value,
  onChange,
  onSubmit,
  submitting,
  mode,
  onCancel,
}: {
  value: PacienteFormValues;
  onChange: (next: PacienteFormValues) => void;
  onSubmit: () => void;
  submitting: boolean;
  mode: "create" | "edit";
  onCancel: () => void;
}) {
  const [errors, setErrors] = useState<FormErrors>({});

  const sexoOptions: readonly SelectOption<PacienteSexo>[] = useMemo(
    () => [
      { value: "NAO_INFORMAR", label: "Não informar" },
      { value: "MASCULINO", label: "Masculino" },
      { value: "FEMININO", label: "Feminino" },
      { value: "OUTRO", label: "Outro" },
    ],
    [],
  );

  function set<K extends keyof PacienteFormValues>(key: K, v: PacienteFormValues[K]) {
    onChange({ ...value, [key]: v });
  }

  function setEndereco<K extends keyof PacienteFormValues["endereco"]>(key: K, v: string) {
    onChange({ ...value, endereco: { ...value.endereco, [key]: v } });
  }

  function handleSubmit() {
    const nextErrors = validatePaciente(value);
    setErrors(nextErrors);
    if (Object.keys(nextErrors).length > 0) return;
    onSubmit();
  }

  return (
    <div className="mf-page-content">
      <div className="pacienteFormGrid">
        <Card>
          <div className="pacienteCardHeader">
            <h2 className="pacienteCardTitle">Dados pessoais</h2>
          </div>

          <div className="pacienteFormFields">
            <div className="pacienteField pacienteSpan3">
              <Input
                label="Primeiro nome"
                value={value.primeiroNome}
                onChange={(ev) => set("primeiroNome", ev.target.value)}
                helperText={errors.primeiroNome}
                placeholder="Ex.: Maria"
                disabled={submitting}
              />
            </div>

            <div className="pacienteField pacienteSpan4">
              <Input
                label="Sobrenome"
                value={value.sobrenome}
                onChange={(ev) => set("sobrenome", ev.target.value)}
                helperText={errors.sobrenome}
                placeholder="Ex.: Silva"
                disabled={submitting}
              />
            </div>

            <div className="pacienteField pacienteSpan3">
              <Input
                label="CPF"
                value={value.cpf}
                onChange={(ev) => set("cpf", formatCPF(ev.target.value))}
                helperText={mode === "edit" ? "CPF não pode ser alterado após o cadastro." : errors.cpf}
                placeholder="000.000.000-00"
                inputMode="numeric"
                disabled={submitting || mode === "edit"}
              />
            </div>

            <div className="pacienteField pacienteSpan3">
              <label className="pacienteLabel">Data de nascimento</label>

              <DateField
                value={value.dataNascimento}
                onChange={(v) => set("dataNascimento", v)}
                aria-label="Data de nascimento"
                disabled={submitting}
              />

              {errors.dataNascimento ? <div className="pacienteHelper">{errors.dataNascimento}</div> : null}
            </div>

            <div className="pacienteField pacienteSpan3">
              <SelectField<PacienteSexo>
                label="Sexo"
                value={value.sexo}
                onChange={(v) => set("sexo", v)}
                options={sexoOptions}
                placeholder="Selecionar..."
                disabled={submitting}
              />
            </div>

            <div className="pacienteField pacienteSpan2">
              <div className="pacienteSwitchControl" data-label="Status">
                <button
                  type="button"
                  className={`pacienteSwitch ${value.ativo ? "isOn" : ""}`}
                  onClick={() => set("ativo", !value.ativo)}
                  aria-pressed={value.ativo}
                  aria-label="Ativo"
                  disabled={submitting || mode === "edit"}
                >
                  <span className="pacienteKnob" />
                </button>
                {mode === "edit" ? <div className="pacienteHelper">Status é controlado fora da edição.</div> : null}
              </div>
            </div>
          </div>
        </Card>

        <Card>
          <div className="pacienteCardHeader">
            <h2 className="pacienteCardTitle">Contato</h2>
          </div>

          <div className="pacienteFormFields">
            <div className="pacienteField pacienteSpan3">
              <Input
                label="Telefone"
                value={value.telefone}
                onChange={(ev) => set("telefone", formatPhoneBR(ev.target.value))}
                helperText={errors.telefone}
                placeholder="(51) 99999-9999"
                inputMode="tel"
                disabled={submitting}
              />
            </div>

            <div className="pacienteField pacienteSpan5">
              <Input
                label="E-mail"
                value={value.email}
                onChange={(ev) => set("email", ev.target.value)}
                helperText={errors.email}
                placeholder="nome@dominio.com"
                inputMode="email"
                disabled={submitting}
              />
            </div>

            <div className="pacienteField pacienteSpan4">
              <Input
                label="Plano de saúde"
                value={value.planoSaude}
                onChange={(ev) => set("planoSaude", ev.target.value)}
                placeholder="Ex.: Unimed"
                disabled={submitting}
              />
            </div>
          </div>
        </Card>

        <Card>
          <div className="pacienteCardHeader">
            <h2 className="pacienteCardTitle">Endereço</h2>
          </div>

          <div className="pacienteFormFields">
            <div className="pacienteField pacienteSpan2 pacienteNarrow">
              <Input
                label="CEP"
                value={value.endereco.cep}
                onChange={(ev) => setEndereco("cep", ev.target.value)}
                helperText={errors["endereco.cep"]}
                placeholder="00000-000"
                inputMode="numeric"
                disabled={submitting}
              />
            </div>

            <div className="pacienteField pacienteSpan7">
              <Input
                label="Logradouro"
                value={value.endereco.logradouro}
                onChange={(ev) => setEndereco("logradouro", ev.target.value)}
                placeholder="Rua / Av."
                disabled={submitting}
              />
            </div>

            <div className="pacienteField pacienteSpan2 pacienteNarrow">
              <Input
                label="Número"
                value={value.endereco.numero}
                onChange={(ev) => setEndereco("numero", ev.target.value)}
                placeholder="123"
                disabled={submitting}
              />
            </div>

            <div className="pacienteField pacienteSpan1 pacienteTiny">
              <Input
                label="UF"
                value={value.endereco.uf}
                onChange={(ev) => setEndereco("uf", ev.target.value.toUpperCase().slice(0, 2))}
                helperText={errors["endereco.uf"]}
                placeholder="RS"
                disabled={submitting}
              />
            </div>

            <div className="pacienteField pacienteSpan4">
              <Input
                label="Complemento"
                value={value.endereco.complemento}
                onChange={(ev) => setEndereco("complemento", ev.target.value)}
                placeholder="Apto, bloco..."
                disabled={submitting}
              />
            </div>

            <div className="pacienteField pacienteSpan4">
              <Input
                label="Bairro"
                value={value.endereco.bairro}
                onChange={(ev) => setEndereco("bairro", ev.target.value)}
                placeholder="Ex.: Petrópolis"
                disabled={submitting}
              />
            </div>

            <div className="pacienteField pacienteSpan4">
              <Input
                label="Cidade"
                value={value.endereco.cidade}
                onChange={(ev) => setEndereco("cidade", ev.target.value)}
                placeholder="Ex.: Porto Alegre"
                disabled={submitting}
              />
            </div>
          </div>
        </Card>
      </div>

      <div className="pacienteActionsBar">
        <div className="pacienteActionsInner">
          <button type="button" className="pacienteLinkBtn" onClick={onCancel} disabled={submitting}>
            Cancelar
          </button>
            <HighlightButton onClick={handleSubmit} disabled={submitting}>
            {submitting ? "Salvando..." : mode === "create" ? "Cadastrar paciente" : "Salvar alterações"}
          </HighlightButton>
        </div>
      </div>
    </div>
  );
}
