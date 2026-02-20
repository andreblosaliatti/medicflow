import { useMemo, useState } from "react";

import Card from "../../../../components/ui/Card";
import Input from "../../../../components/form/Input";
import SelectField, { type SelectOption } from "../../../../components/form/SelectField/SelectField";
import HighlightButton from "../../../../components/ui/HighlightButton/HighlightButton";

import type { PacienteDTO, Sexo } from "../../../../mocks/db/seed";
import { formatCPF, formatPhoneBR, type FormErrors, validatePaciente } from "./formUtils";
import DateField from "../../../../components/form/DateField/DateField";
import styles from "./PacienteForm.module.css";

export default function PacienteForm({
  value,
  onChange,
  onSubmit,
  submitting,
  mode,
  onCancel,
}: {
  value: PacienteDTO;
  onChange: (next: PacienteDTO) => void;
  onSubmit: () => void;
  submitting: boolean;
  mode: "create" | "edit";
  onCancel: () => void;
}) {
  const [errors, setErrors] = useState<FormErrors>({});

  const sexoOptions: readonly SelectOption<Sexo>[] = useMemo(
    () => [
      { value: "NAO_INFORMAR", label: "Não informar" },
      { value: "MASCULINO", label: "Masculino" },
      { value: "FEMININO", label: "Feminino" },
      { value: "OUTRO", label: "Outro" },
    ],
    []
  );

  function set<K extends keyof PacienteDTO>(key: K, v: PacienteDTO[K]) {
    onChange({ ...value, [key]: v });
  }

  function setEndereco<K extends keyof PacienteDTO["endereco"]>(key: K, v: string) {
    onChange({ ...value, endereco: { ...value.endereco, [key]: v } });
  }

  function handleSubmit() {
    const e = validatePaciente(value);
    setErrors(e);
    if (Object.keys(e).length > 0) return;
    onSubmit();
  }

  return (
    <div className={styles.page}>
      <div className={styles.grid}>
        {/* =========================
            DADOS PESSOAIS
           ========================= */}
        <Card>
          <div className={styles.cardHeader}>
            <h2 className={styles.cardTitle}>Dados pessoais</h2>
          </div>

          <div className={styles.formGrid}>
            <div className={`${styles.field} ${styles.span3}`}>
              <Input
                label="Primeiro nome"
                value={value.primeiroNome}
                onChange={(ev) => set("primeiroNome", ev.target.value)}
                helperText={errors.primeiroNome}
                placeholder="Ex.: Maria"
              />
            </div>

            <div className={`${styles.field} ${styles.span4}`}>
              <Input
                label="Sobrenome"
                value={value.sobrenome}
                onChange={(ev) => set("sobrenome", ev.target.value)}
                helperText={errors.sobrenome}
                placeholder="Ex.: Silva"
              />
            </div>

            <div className={`${styles.field} ${styles.span3}`}>
              <Input
                label="CPF"
                value={value.cpf}
                onChange={(ev) => set("cpf", formatCPF(ev.target.value))}
                helperText={errors.cpf}
                placeholder="000.000.000-00"
                inputMode="numeric"
              />
            </div>

            <div className={`${styles.field} ${styles.span3}`}>
              <label className={styles.label}>Data de nascimento</label>

              <DateField
                value={value.dataNascimento}
                onChange={(v) => set("dataNascimento", v)}
                aria-label="Data de nascimento"
              />

              {errors.dataNascimento && (
                <div className={styles.helper}>{errors.dataNascimento}</div>
              )}
            </div>

            <div className={`${styles.field} ${styles.span3}`}>
              <SelectField<Sexo>
                label="Sexo"
                value={value.sexo}
                onChange={(v) => set("sexo", v)}
                options={sexoOptions}
                placeholder="Selecionar..."
              />
            </div>

            <div className={`${styles.field} ${styles.span2}`}>
              <div className={styles.switchControl} data-label="Ativo">
                <button
                  type="button"
                  className={`${styles.switch} ${value.ativo ? styles.on : ""}`}
                  onClick={() => set("ativo", !value.ativo)}
                  aria-pressed={value.ativo}
                  aria-label="Ativo"
                >
                  <span className={styles.knob} />
                </button>
              </div>
            </div>
          </div>
        </Card>

        {/* =========================
            CONTATO
           ========================= */}
        <Card>
          <div className={styles.cardHeader}>
            <h2 className={styles.cardTitle}>Contato</h2>
          </div>

          <div className={styles.formGrid}>
            <div className={`${styles.field} ${styles.span3}`}>
              <Input
                label="Telefone"
                value={value.telefone}
                onChange={(ev) => set("telefone", formatPhoneBR(ev.target.value))}
                helperText={errors.telefone}
                placeholder="(51) 99999-9999"
                inputMode="tel"
              />
            </div>

            <div className={`${styles.field} ${styles.span5}`}>
              <Input
                label="E-mail"
                value={value.email}
                onChange={(ev) => set("email", ev.target.value)}
                helperText={errors.email}
                placeholder="nome@dominio.com"
                inputMode="email"
              />
            </div>

            <div className={`${styles.field} ${styles.span4}`}>
              <Input
                label="Plano de saúde"
                value={value.planoSaude}
                onChange={(ev) => set("planoSaude", ev.target.value)}
                placeholder="Ex.: Unimed"
              />
            </div>
          </div>
        </Card>

        {/* =========================
            ENDEREÇO
           ========================= */}
        <Card>
          <div className={styles.cardHeader}>
            <h2 className={styles.cardTitle}>Endereço</h2>
          </div>

          <div className={styles.formGrid}>
            <div className={`${styles.field} ${styles.span2} ${styles.narrow}`}>
              <Input
                label="CEP"
                value={value.endereco.cep}
                onChange={(ev) => setEndereco("cep", ev.target.value)}
                helperText={errors["endereco.cep"]}
                placeholder="00000-000"
                inputMode="numeric"
              />
            </div>

            <div className={`${styles.field} ${styles.span7}`}>
              <Input
                label="Logradouro"
                value={value.endereco.logradouro}
                onChange={(ev) => setEndereco("logradouro", ev.target.value)}
                placeholder="Rua / Av."
              />
            </div>

            <div className={`${styles.field} ${styles.span2} ${styles.narrow}`}>
              <Input
                label="Número"
                value={value.endereco.numero}
                onChange={(ev) => setEndereco("numero", ev.target.value)}
                placeholder="123"
              />
            </div>

            <div className={`${styles.field} ${styles.span1} ${styles.tiny}`}>
              <Input
                label="UF"
                value={value.endereco.uf}
                onChange={(ev) => setEndereco("uf", ev.target.value.toUpperCase().slice(0, 2))}
                helperText={errors["endereco.uf"]}
                placeholder="RS"
              />
            </div>

            <div className={`${styles.field} ${styles.span4}`}>
              <Input
                label="Complemento"
                value={value.endereco.complemento}
                onChange={(ev) => setEndereco("complemento", ev.target.value)}
                placeholder="Apto, bloco..."
              />
            </div>

            <div className={`${styles.field} ${styles.span4}`}>
              <Input
                label="Bairro"
                value={value.endereco.bairro}
                onChange={(ev) => setEndereco("bairro", ev.target.value)}
                placeholder="Ex.: Petrópolis"
              />
            </div>

            <div className={`${styles.field} ${styles.span4}`}>
              <Input
                label="Cidade"
                value={value.endereco.cidade}
                onChange={(ev) => setEndereco("cidade", ev.target.value)}
                placeholder="Ex.: Porto Alegre"
              />
            </div>
          </div>
        </Card>
      </div>

      {/* =========================
          ACTIONS BAR
         ========================= */}
      <div className={styles.actionsBar}>
        <div className={styles.actionsInner}>
          <button type="button" className={styles.linkBtn} onClick={onCancel}>
            Cancelar
          </button>

          <HighlightButton onClick={handleSubmit} disabled={submitting}>
            {submitting ? "Salvando..." : mode === "create" ? "Salvar paciente" : "Salvar alterações"}
          </HighlightButton>
        </div>
      </div>
    </div>
  );
}
