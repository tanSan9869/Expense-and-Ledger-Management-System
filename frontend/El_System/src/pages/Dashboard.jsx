// src/pages/Dashboard.jsx
import { useEffect, useState } from "react";
import { useAuth } from "../hooks/useAuth";
import { useLocation } from "react-router-dom";
import { getLedgerBalance, getCategorySummary, getExpenses } from "../api/services";
import CategorySummary from "../components/CategorySummary";

export default function Dashboard() {
  const { user } = useAuth();
  const location = useLocation(); 
  const [balance, setBalance] = useState(null);
  const [summary, setSummary] = useState([]);
  const [recentExpenses, setRecentExpenses] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAll = async () => {
      try {
        const [balRes, sumRes, expRes] = await Promise.all([
          getLedgerBalance(user.userId),
          getCategorySummary(user.userId),
          getExpenses(user.userId, { page: 0, size: 5, sortBy: "expenseDate", sortDir: "desc" }),
        ]);
        setBalance(balRes.data);
        setSummary(sumRes.data);
        setRecentExpenses(expRes.data.content);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchAll();
  }, [user.userId,location.key]);

  if (loading) return (
    <div className="flex justify-center items-center h-48">
      <div className="w-8 h-8 border-4 border-blue-500 border-t-transparent rounded-full animate-spin" />
    </div>
  );

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-semibold text-slate-800">Dashboard</h1>
        <p className="text-slate-500 text-sm mt-1">
          Welcome back, {user.username}
        </p>
      </div>

      {/* Stats row */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="bg-slate-900 text-white rounded-2xl p-6 col-span-1">
          <p className="text-slate-400 text-xs uppercase tracking-widest mb-2">
            Current Balance
          </p>
          <p className={`text-3xl font-bold ${balance >= 0 ? "text-emerald-400" : "text-red-400"}`}>
            ₹{Number(balance).toFixed(2)}
          </p>
        </div>

        <div className="bg-white border border-slate-200 rounded-2xl p-6">
          <p className="text-slate-400 text-xs uppercase tracking-widest mb-2">
            Total Expenses
          </p>
          <p className="text-3xl font-bold text-slate-800">
            {recentExpenses.length}
          </p>
          <p className="text-slate-400 text-xs mt-1">this month</p>
        </div>

        <div className="bg-white border border-slate-200 rounded-2xl p-6">
          <p className="text-slate-400 text-xs uppercase tracking-widest mb-2">
            Categories Used
          </p>
          <p className="text-3xl font-bold text-slate-800">{summary.length}</p>
          <p className="text-slate-400 text-xs mt-1">across all time</p>
        </div>
      </div>

      {/* Category summary + recent expenses side by side */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white border border-slate-200 rounded-2xl p-6">
          <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-widest mb-5">
            Spending by Category
          </h2>
          <CategorySummary data={summary} />
        </div>

        <div className="bg-white border border-slate-200 rounded-2xl p-6">
          <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-widest mb-5">
            Recent Expenses
          </h2>
          {recentExpenses.length === 0 ? (
            <p className="text-slate-400 text-sm text-center py-8">
              No expenses yet. Add your first one.
            </p>
          ) : (
            <div className="space-y-3">
              {recentExpenses.map((exp) => (
                <div key={exp.id} className="flex items-center justify-between py-2 border-b border-slate-100 last:border-0">
                  <div>
                    <p className="text-sm font-medium text-slate-700">
                      {exp.description}
                    </p>
                    <p className="text-xs text-slate-400 mt-0.5">
                      {exp.expenseDate} · {exp.categoryName}
                    </p>
                  </div>
                  <span className="text-sm font-semibold text-red-500">
                    ₹{Number(exp.amount).toFixed(2)}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}