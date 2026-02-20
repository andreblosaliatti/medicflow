import { useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

import AppPage from "../../../components/layout/AppPage/AppPage";
import PageHeader from "../../../components/layout/PageHeader/PageHeader";
import Panel from "../../../components/ui/Panel/Panel";

import Input from "../../../components/form/Input";
import SelectField from "../../../components/form/SelectField/SelectField";

import PacientsRow, {
  type PacienteModel,
  type PacienteMenuAction,
} from "../../../components/pacients/PacientsRow/PacientsRow";

import { TableWrap, Table, THead, TBody, Th } from "../../../components/ui/Table/Table";

import {
  ordenarOptions,
  filtroOptions,
  exibindoOptions,
  type OrdenarPor,
  type FiltroConvenio,
  type Exibindo,
} from "./pacientFilters";

import { toPacientesRows } from "../../../mocks/mappers"; // ✅ fonte única

import "./styles.css";

type NavState = { from?: string };

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

  // ✅ fonte única (seed + storage via mocks)
  const pacientesBase = useMemo(() => toPacientesRows() as PacienteModel[], []);

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

  function onAction(pacienteId: number, action: PacienteMenuAction) {
    setOpenMenuId(null);

    const routes: Record<PacienteMenuAction, string | null> = {
      PRONTUARIO: `/pacientes/${pacienteId}/prontuario`,
      VER_PERFIL: `/pacientes/${pacienteId}`,
      EDITAR: `/pacientes/${pacienteId}/editar`,
      NOVA_CONSULTA: `/agenda?pacienteId=${encodeURIComponent(String(pacienteId))}`,
      ENVIAR_MENSAGEM: `/comunicacao?pacienteId=${encodeURIComponent(String(pacienteId))}`,
      ARQUIVAR: null,
    };

    const to = routes[action];

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
                  <Th style={{ width: 110 }} align="center">
                    Ações
                  </Th>
                </tr>
              </THead>

              <TBody>
                {pageItems.map((p) => (
                  <PacientsRow
                    key={p.id}
                    paciente={p}
                    menuOpen={openMenuId === p.id}
                    onToggleMenu={(id) => setOpenMenuId((prev) => (prev === id ? null : id))}
                    onCloseMenu={() => setOpenMenuId(null)}
                    onAction={onAction}
                  />
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
    </AppPage>
  );
}