import Sidebar from "./SideBar";
import Topbar from "./TopBar/TopBar";
import { Outlet } from "react-router-dom";

type Props = {
  showSearch?: boolean;
  searchPlaceholder?: string;
};

export default function AppLayout({
  showSearch = true,
  searchPlaceholder = "Buscar...",
}: Props) {
  return (
    <div className="mf-app">
      <Sidebar />

      <div className="mf-app__main">
        <Topbar showSearch={showSearch} searchPlaceholder={searchPlaceholder} />
        <main className="mf-app__content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}