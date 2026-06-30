// src/pages/Expenses.jsx
import { useEffect, useState, useCallback } from "react";
import { useAuth } from "../hooks/useAuth";
import { getExpenses, deleteExpense } from "../api/services";
import ExpenseForm from "../components/ExpenseForm";
import ExpenseTable from "../components/ExpenseTable";

export default function Expenses() {
  const { user } = useAuth();
  const [data, setData] = useState({
    content: [],
    totalPages: 0,
    totalElements: 0,
  });
  const [filters, setFilters] = useState({
    page: 0,
    size: 10,
    sortBy: "expenseDate",
    sortDir: "desc",
    keyword: "",
    categoryId: "",
    startDate: "",
    endDate: "",
  });
  const [showForm, setShowForm] = useState(false);
  const [editTarget, setEditTarget] = useState(null);
  const [loading, setLoading] = useState(false);

  const fetchExpenses = useCallback(async () => {
    setLoading(true);
    try {
      const params = Object.fromEntries(
        Object.entries(filters).filter(([, v]) => v !== "" && v !== null),
      );
      const res = await getExpenses(user.userId, params);
      setData(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [filters, user.userId]);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchExpenses();
  }, [fetchExpenses]);

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this expense?")) return;
    await deleteExpense(id);
    fetchExpenses();
  };

  const handleFormSuccess = () => {
    setShowForm(false);
    setEditTarget(null);
    fetchExpenses();
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-800">Expenses</h1>
          <p className="text-slate-500 text-sm mt-1">
            {data.totalElements} total records
          </p>
        </div>
        <button
          onClick={() => {
            
            setEditTarget(null);
            setShowForm(true);
          }}
          className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium rounded-lg transition-colors"
        >
          + Add Expense
        </button>
      </div>

      {/* Filter bar */}
      <div className="bg-white border border-slate-200 rounded-2xl p-4">
        <div className="flex flex-wrap gap-3">
          <input
            type="text"
            placeholder="Search description..."
            className="px-3.5 py-2 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 min-w-48"
            value={filters.keyword}
            onChange={(e) =>
              setFilters({ ...filters, keyword: e.target.value, page: 0 })
            }
          />
          <input
            type="date"
            className="px-3.5 py-2 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={filters.startDate}
            onChange={(e) =>
              setFilters({ ...filters, startDate: e.target.value, page: 0 })
            }
          />
          <input
            type="date"
            className="px-3.5 py-2 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={filters.endDate}
            onChange={(e) =>
              setFilters({ ...filters, endDate: e.target.value, page: 0 })
            }
          />
          <button
            onClick={() =>
              setFilters({
                ...filters,
                keyword: "",
                startDate: "",
                endDate: "",
                categoryId: "",
                page: 0,
              })
            }
            className="px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-600 text-sm rounded-lg transition-colors"
          >
            Clear filters
          </button>
        </div>
      </div>

      {/* Table */}
      <div className="bg-white border border-slate-200 rounded-2xl overflow-hidden">
        {loading ? (
          <div className="flex justify-center items-center h-40">
            <div className="w-7 h-7 border-4 border-blue-500 border-t-transparent rounded-full animate-spin" />
          </div>
        ) : (
          <>
            <table className="w-full text-sm">
              <thead>
                <tr className="bg-slate-50 border-b border-slate-200">
                  {["Date", "Description", "Category", "Amount", "Actions"].map(
                    (h) => (
                      <th
                        key={h}
                        className="text-left px-5 py-3.5 text-xs font-semibold text-slate-500 uppercase tracking-wider"
                      >
                        {h}
                      </th>
                    ),
                  )}
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                <ExpenseTable
                  expenses={data.content}
                  onEdit={(exp) => {
                    setEditTarget(exp);
                    setShowForm(true);
                  }}
                  onDelete={handleDelete}
                />
              </tbody>
            </table>

            {/* Pagination */}
            {data.totalPages > 1 && (
              <div className="flex items-center justify-between px-5 py-3.5 border-t border-slate-100 bg-slate-50">
                <span className="text-xs text-slate-500">
                  Page {filters.page + 1} of {data.totalPages}
                </span>
                <div className="flex gap-2">
                  <button
                    disabled={filters.page === 0}
                    onClick={() =>
                      setFilters({ ...filters, page: filters.page - 1 })
                    }
                    className="px-3 py-1.5 text-xs border border-slate-200 rounded-lg disabled:opacity-40 hover:bg-white transition-colors"
                  >
                    ← Prev
                  </button>
                  <button
                    disabled={filters.page + 1 >= data.totalPages}
                    onClick={() =>
                      setFilters({ ...filters, page: filters.page + 1 })
                    }
                    className="px-3 py-1.5 text-xs border border-slate-200 rounded-lg disabled:opacity-40 hover:bg-white transition-colors"
                  >
                    Next →
                  </button>
                </div>
              </div>
            )}
          </>
        )}
      </div>

      {showForm && (
        <ExpenseForm
          existing={editTarget}
          onSuccess={handleFormSuccess}
          onCancel={() => {
            setShowForm(false);
            setEditTarget(null);
          }}
        />
      )}
    </div>
  );
}
