import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./views/pages/Login/LoginPage";
import ForgotPasswordPage from "./views/pages/ForgotPassword/ForgotPasswordPage";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="*" element={<Navigate to="/login" />} />
        <Route path="/esqueci-senha" element={<ForgotPasswordPage />} />
      </Routes>
    </BrowserRouter>
  );
}
