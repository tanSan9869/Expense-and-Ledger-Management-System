// src/components/Navbar.jsx
import { Link, useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => { logout(); navigate("/login"); };

  const navLink = (to, label) => (
    <Link
      to={to}
      className={`text-sm font-medium px-3 py-1.5 rounded-lg transition-colors ${
        location.pathname === to
          ? "bg-blue-600 text-white"
          : "text-slate-300 hover:text-white hover:bg-slate-700"
      }`}
    >
      {label}
    </Link>
  );

  return (
    <nav className="bg-slate-900 border-b border-slate-800 sticky top-0 z-40">
      <div className="max-w-6xl mx-auto px-4 h-14 flex items-center justify-between">
        <span className="text-white font-semibold text-base tracking-tight">
          💰 Expense Ledger
        </span>
        {user && (
          <div className="flex items-center gap-2">
            {navLink("/dashboard", "Dashboard")}
            {navLink("/expenses", "Expenses")}
            {navLink("/ledger", "Ledger")}
            {navLink("/categories", "Categories")}
            <span className="text-slate-500 text-sm ml-2">
              {user.username}
            </span>
            <button
              onClick={handleLogout}
              className="ml-1 text-sm px-3 py-1.5 bg-red-600 hover:bg-red-700 text-white rounded-lg transition-colors"
            >
              Logout
            </button>
          </div>
        )}
      </div>
    </nav>
  );
}