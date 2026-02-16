import "./topbar.css";

export default function Topbar({ showSearch = true, searchPlaceholder = "Buscar..." }) {
  return (
    <header className="mf-topbar">
      <div className="mf-topbar__left">
        {showSearch && (
          <div className="mf-topbar__search">
            <input placeholder={searchPlaceholder} />
          </div>
        )}
      </div>

      <div className="mf-topbar__right">
        <button className="mf-user" type="button">
          <div className="mf-user__avatar">JC</div>

          <div className="mf-user__info">
            <span className="mf-user__name">Dr. João Carvalho</span>
            <span className="mf-user__role">Médico</span>
          </div>

          <span className="mf-user__chevron">▾</span>
        </button>
      </div> 
    </header>
  );
}
