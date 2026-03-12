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

import { toPacientesRows } from "../../../mocks/mappers";

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

type PacienteRowModel = {
  id: number;
  nome: string;
  telefone: string;
  ultimaConsulta: string;
  convenio: string;
  initials?: string;
};

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

  const from = `${location.pathname}${location.search}`;
  const navState: NavState = { from };

  const pacientesBase = useMemo(() => toPacientesRows() as PacienteRowModel[], []);

  const listaFiltrada = useMemo(() => {
    const q = query.trim().toLowerCase();
    let data = [...pacientesBase];

    if (q) {
      data = data.filter((p) => {
        const base = `${p.nome} ${p.telefone} ${p.ultimaConsulta} ${p.convenio}`.toLowerCase();
        return base.includes(q);
      });
    }

    if (filtroConvenio !== "TODOS") {
      data = data.filter((p) => {
        const tem = p.convenio.toLowerCase() !== "nunca" && p.convenio.trim() !== "";
        return filtroConvenio === "COM" ? tem : !tem;
      });
    }

    // ainda não aplicado (mantém para não quebrar UI)
    void exibindo;

    data.sort((a, b) => {
      if (ordenarPor === "NOME") return a.nome.localeCompare(b.nome);
      return a.ultimaConsulta.localeCompare(b.ultimaConsulta);
    });

    return data;
  }, [pacientesBase, query, ordenarPor, filtroConvenio, exibindo]);

  const total = listaFiltrada.length;
  const totalPages = Math.max(1, Math.ceil(total / pageSize));
  const safePage = Math.min(page, totalPages);

  const start = (safePage - 1) * pageSize;
  const end = Math.min(start + pageSize, total);
  const pageItems = listaFiltrada.slice(start, end);

  function initials(name: string) {
    const parts = name.trim().split(/\s+/).slice(0, 2);
    return parts.map((p) => p[0]?.toUpperCase()).join("");
  }

  function onAction(pacienteId: number, action: PacienteMenuAction) {
    setOpenMenuId(null);

    // ✅ Nova consulta: navega para Agenda e pede abertura do drawer com paciente travado
    if (action === "NOVA_CONSULTA") {
      const paciente = pacientesBase.find((x) => x.id === pacienteId);

      navigate("/agenda", {
        state: {
          from: navState.from,
          novaConsulta: {
            pacienteId: String(pacienteId),
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
                  onChange={(v) => {
                    setOrdenarPor(v);
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
                  onChange={(v) => {
                    setFiltroConvenio(v);
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
                  onChange={(v) => {
                    setExibindo(v);
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
                  onChange={(e) => {
                    setQuery(e.target.value);
                    setPage(1);
                  }}
                  placeholder="Buscar paciente..."
                />
              </div>
            </div>
          }
        >
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
                {pageItems.map((p) => (
                  <Tr
                    key={p.id}
                    onClick={() => navigate(`/pacientes/${p.id}`, { state: navState })}
                    ariaLabel={`Abrir perfil de ${p.nome}`}
                  >
                    <Td>
                      <div className="mf-person">
                        <div className="mf-avatar" aria-hidden="true">
                          {p.initials ?? initials(p.nome)}
                        </div>
                        <span className="mf-person__name">{p.nome}</span>
                      </div>
                    </Td>

                    <Td className="mf-mono">{p.telefone}</Td>
                    <Td className="mf-mono">{p.ultimaConsulta}</Td>
                    <Td className="mf-muted">{p.convenio}</Td>

                    <Td align="right" onClick={(e) => e.stopPropagation()}>
                      <div className="mf-row-actions">
                        <div className="mf-split">
                          <div className="mf-split__buttons">
                            <button
                              type="button"
                              className="mf-split__main"
                              onClick={() => onAction(p.id, "PRONTUARIO")}
                            >
                              📄 <span>Prontuário</span>
                            </button>

                            <button
                              type="button"
                              className="mf-split__caret"
                              aria-label="Mais ações"
                              onClick={() => setOpenMenuId((prev) => (prev === p.id ? null : p.id))}
                            >
                              ▾
                            </button>
                          </div>

                          <RowMenu
                            open={openMenuId === p.id}
                            onClose={() => setOpenMenuId(null)}
                            items={[
                              { key: "PRESCRICOES", label: "Prescrições" },
                              { key: "VER_PERFIL", label: "Ver perfil" },
                              { key: "EDITAR", label: "Editar", tone: "primary" },
                              { key: "NOVA_CONSULTA", label: "Nova consulta" },
                              { key: "ENVIAR_MENSAGEM", label: "Enviar mensagem" },
                              { key: "ARQUIVAR", label: "Arquivar", tone: "danger" },
                            ]}
                            onSelect={(key) => onAction(p.id, key as PacienteMenuAction)}
                          />
                        </div>
                      </div>
                    </Td>
                  </Tr>
                ))}
              </TBody>
            </Table>
          </TableWrap>

          {total === 0 ? (
            <div className="mf-empty">
              <div className="mf-empty__title">Nenhum paciente encontrado</div>
              <div className="mf-empty__sub">Ajuste a busca ou os filtros.</div>
            </div>
          ) : null}

          <div className="mf-pager">
            <div className="mf-pager__left">
              {total > 0 ? (
                <span>
                  {start + 1} - {end} de {total}
                </span>
              ) : (
                <span>0 de 0</span>
              )}
            </div>

            <div className="mf-pager__right">
              <button
                type="button"
                className="mf-pager__btn"
                disabled={safePage <= 1}
                onClick={() => setPage((p) => Math.max(1, p - 1))}
                aria-label="Anterior"
              >
                ‹
              </button>

              <div className="mf-pager__page">{safePage}</div>

              <button
                type="button"
                className="mf-pager__btn"
                disabled={safePage >= totalPages}
                onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
                aria-label="Próximo"
              >
                ›
              </button>
            </div>
          </div>
        </Panel>
      </div>
    </AppPage>
  );
}