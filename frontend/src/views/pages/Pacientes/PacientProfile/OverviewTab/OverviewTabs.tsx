import Card from "../../../../../components/ui/Card";

export default function OverviewTab() {
  return (
    <div className="patient-overview-grid">
  <div className="patient-overview-card">
    <Card>
      <h3>Dados Pessoais</h3>
      <p>Data de Nascimento: 24/03/1988</p>
      <p>Sexo: Feminino</p>
      <p>Convênio: Nenhum</p>
    </Card>
  </div>

  <div className="patient-overview-card">
    <Card>
      <h3>Contato e Endereço</h3>
      <p>Telefone: (11) 98765-4321</p>
      <p>Endereço: Rua das Flores, 123</p>
      <p>São Paulo - SP</p>
    </Card>
  </div>
</div>
  );
}