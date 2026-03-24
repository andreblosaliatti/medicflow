import { useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

import AppPage from "../../../components/layout/AppPage/AppPage";
import PageHeader from "../../../components/layout/PageHeader/PageHeader";
import Panel from "../../../components/ui/Panel/Panel";

import Input from "../../../components/form/Input";
import SelectField from "../../../components/form/SelectField/SelectField";
import RowMenu from "../../../components/ui/RowMenu/RowMenu";

import { TableWrap, Table, THead, TBody, Tr, Th, Td } from "../../../components/ui/Table/Table";

import {
  ordenarOptions,
  filtroOptions,
  exibindoOptions,
  type OrdenarPor,
  type FiltroConvenio,
  type Exibindo,
} from "./pacientFilters";

import { usePacientesQuery } from "../../../api/pacientes/hooks";
import type { PacienteRowViewModel } from "../../../api/pacientes/types";

import "./styles.css";

type NavState = { from?: string };

type PacienteMenuAction =
  | "PRONTUARIO"
  | "PRESCRICOES"
  | "VER_PERFIL"
  | "EDITAR"
  | "NOVA_CONSULTA"
  | "ENVIAR_MENSAGEM"
  | "ARQUIVAR";

export default function PacientesPage() {
  const navigate = useNavigate();
  const location = useLocation();

  const [query, setQuery] = useState("");
  const [ordenarPor, setOrdenarPor] = useState<OrdenarPor>("NOME");
  const [filtroConvenio, setFiltroConvenio] = useState<FiltroConvenio>("TODOS");
  const [exibindo, setExibindo] = useState<Exibindo>("TODOS");

  const [page, setPage] = useState(1);
  const pageSize = 10;

  const [openMenuId, setOpenMenuId] = useState<number | null>(null);
  const { data: pacientesPage, isLoading, error } = usePacientesQuery();
  const pacientesBase = pacientesPage.content;

  const from = `${location.pathname}${location.search}`;
  const navState: NavState = { from };

  const listaFiltrada = useMemo(() => {
    const q = query.trim().toLowerCase();
    let data = [...pacientesBase];

    if (q) {
      data = data.filter((paciente) => {
        const base = `${paciente.nome} ${paciente.telefone} ${paciente.ultimaConsulta} ${paciente.convenio}`.toLowerCase();
        return base.includes(q);
      });
    }

    if (filtroConvenio !== "TODOS") {
      data = data.filter((paciente) => {
        const temConvenio = paciente.convenio.toLowerCase() !== "não informado" && paciente.convenio.trim() !== "";
        return filtroConvenio === "COM" ? temConvenio : !temConvenio;
      });
    }

    void exibindo;

    data.sort((pacienteA, pacienteB) => {
      if (ordenarPor === "NOME") return pacienteA.nome.localeCompare(pacienteB.nome);
      return pacienteA.ultimaConsulta.localeCompare(pacienteB.ultimaConsulta);
    });

    return data;
  }, [pacientesBase, query, ordenarPor, filtroConvenio, exibindo]);

  const total = listaFiltrada.length;
  const totalPages = Math.max(1, Math.ceil(total / pageSize));
  const safePage = Math.min(page, totalPages);

  const start = (safePage - 1) * pageSize;
  const end = Math.min(start + pageSize, total);
  const pageItems = listaFiltrada.slice(start, end);

  function onAction(pacienteId: number, action: PacienteMenuAction) {
    setOpenMenuId(null);

    if (action === "NOVA_CONSULTA") {
      const paciente = pacientesBase.find((item) => item.id === pacienteId);

      navigate("/agenda", {
        state: {
          from: navState.from,
          novaConsulta: {
            pacienteId,
            pacienteNome: paciente?.nome ?? "",
          },
        },
      });

      return;
    }

    const routes: Record<Exclude<PacienteMenuAction, "NOVA_CONSULTA">, string | null> = {
      PRONTUARIO: `/pacientes/${pacienteId}/prontuario`,
      PRESCRICOES: `/pacientes/${pacienteId}/prescricoes`,
      VER_PERFIL: `/pacientes/${pacienteId}`,
      EDITAR: `/pacientes/${pacienteId}/editar`,
      ENVIAR_MENSAGEM: `/comunicacao?pacienteId=${encodeURIComponent(String(pacienteId))}`,
      ARQUIVAR: null,
    };

    const to = routes[action as Exclude<PacienteMenuAction, "NOVA_CONSULTA">];
    if (to) {
      navigate(to, { state: navState });
      return;
    }

    console.log("Arquivar paciente:", pacienteId);
  }

  return (
    <AppPage
      onClick={() => setOpenMenuId(null)}
      header={
        <PageHeader
          title="Pacientes"
          actions={[
            {
              label: "Novo Paciente",
              icon: "➕",
              variant: "primary",
              onClick: () => navigate("/pacientes/novo", { state: navState }),
            },
          ]}
        />
      }
    >
      <div className="mf-page-content">
        <Panel
          title="Lista de Pacientes"
          icon="👥"
          right={
            <div className="mf-controls">
              <div className="mf-control">
                <span className="mf-control__label">Ordenar por</span>
                <SelectField<OrdenarPor>
                  value={ordenarPor}
                  onChange={(value) => {
                    setOrdenarPor(value);
                    setPage(1);
                  }}
                  options={ordenarOptions}
                  ariaLabel="Ordenar por"
                  className="mf-select--embedded"
                />
              </div>

              <div className="mf-control">
                <span className="mf-control__label">Filtro</span>
                <SelectField<FiltroConvenio>
                  value={filtroConvenio}
                  onChange={(value) => {
                    setFiltroConvenio(value);
                    setPage(1);
                  }}
                  options={filtroOptions}
                  ariaLabel="Filtro"
                  className="mf-select--embedded"
                />
              </div>

              <div className="mf-control">
                <span className="mf-control__label">Exibindo</span>
                <SelectField<Exibindo>
                  value={exibindo}
                  onChange={(value) => {
                    setExibindo(value);
                    setPage(1);
                  }}
                  options={exibindoOptions}
                  ariaLabel="Exibindo"
                  className="mf-select--embedded"
                />
              </div>

              <div className="mf-search">
                <Input
                  value={query}
                  onChange={(event) => {
                    setQuery(event.target.value);
                    setPage(1);
                  }}
                  placeholder="Buscar paciente..."
                />
              </div>
            </div>
          }
        >
          {error ? <div className="mf-muted">{error}</div> : null}

          <TableWrap>
            <Table>
              <THead>
                <tr>
                  <Th>Nome</Th>
                  <Th style={{ width: 180 }}>Telefone</Th>
                  <Th style={{ width: 170 }}>Última Consulta</Th>
                  <Th style={{ width: 150 }}>Convênio</Th>
                  <Th style={{ width: 180 }} align="right">
                    Ações
                  </Th>
                </tr>
              </THead>

              <TBody>
                {!isLoading && pageItems.length === 0 ? (
                  <Tr>
                    <Td colSpan={5} className="mf-muted">
                      Nenhum paciente encontrado.
                    </Td>
                  </Tr>
                ) : null}

                {pageItems.map((paciente: PacienteRowViewModel) => (
                  <Tr
                    key={paciente.id}
                    onClick={() => navigate(`/pacientes/${paciente.id}`, { state: navState })}
                    ariaLabel={`Abrir perfil de ${paciente.nome}`}
                  >
                    <Td>
                      <div className="mf-person">
                        <div className="mf-avatar" aria-hidden="true">
                          {paciente.initials}
                        </div>
                        <span className="mf-person__name">{paciente.nome}</span>
                      </div>
                    </Td>

                    <Td className="mf-mono">{paciente.telefone}</Td>
                    <Td className="mf-mono">{paciente.ultimaConsulta}</Td>
                    <Td className="mf-muted">{paciente.convenio}</Td>

                    <Td align="right" onClick={(event) => event.stopPropagation()}>
                      <div className="mf-row-actions">
                        <div className="mf-split">
                          <button
                            type="button"
                            className="mf-btn-link"
                            onClick={() => navigate(`/pacientes/${paciente.id}`)}
                          >
                            Ver perfil
                          </button>

                          <button
                            type="button"
                            className="mf-rowMenuBtn"
                            aria-label="Ações do paciente"
                            onClick={() => setOpenMenuId(openMenuId === paciente.id ? null : paciente.id)}
                          >
                            ⋯
                          </button>
                        </div>

                        <RowMenu
                          open={openMenuId === paciente.id}
                          onClose={() => setOpenMenuId(null)}
                          items={[
                            { key: "PRONTUARIO", label: "Prontuário" },
                            { key: "PRESCRICOES", label: "Prescrições" },
                            { key: "VER_PERFIL", label: "Ver perfil" },
                            { key: "EDITAR", label: "Editar", tone: "primary" },
                            { key: "NOVA_CONSULTA", label: "Nova consulta" },
                            { key: "ENVIAR_MENSAGEM", label: "Enviar mensagem" },
                            { key: "ARQUIVAR", label: "Arquivar", tone: "danger" },
                          ]}
                          onSelect={(key) => onAction(paciente.id, key as PacienteMenuAction)}
                        />
                      </div>
                    </Td>
                  </Tr>
                ))}
              </TBody>
            </Table>
          </TableWrap>

          <div className="mf-pagination">
            <span className="mf-pagination__text">
              {isLoading ? "Carregando pacientes..." : `Exibindo ${total === 0 ? 0 : start + 1}-${end} de ${total}`}
            </span>

            <div className="mf-pagination__actions">
              <button type="button" className="mf-pageBtn" disabled={safePage <= 1} onClick={() => setPage((currentPage) => currentPage - 1)}>
                Anterior
              </button>
              <span className="mf-pageIndex">Página {safePage}</span>
              <button
                type="button"
                className="mf-pageBtn"
                disabled={safePage >= totalPages}
                onClick={() => setPage((currentPage) => currentPage + 1)}
              >
                Próxima
              </button>
            </div>
          </div>
        </Panel>
      </div>
    </AppPage>
  );
}
