// src/components/ExpenseForm.jsx
import { useEffect, useState } from "react";
import { getCategories, createExpense, updateExpense } from "../api/services";
import { useAuth } from "../hooks/useAuth";


export default function ExpenseForm({ existing, onSuccess, onCancel }) {
  const { user } = useAuth();
  const [categories, setCategories] = useState([]);
  const [form, setForm] = useState({
    categoryId: "",
    amount: "",
    description: "",
    expenseDate: new Date().toISOString().split("T")[0],
  });
  const [errors, setErrors] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
  getCategories()
    .then((res) => {
      console.log("Categories fetched:", res.data);  // add this
      setCategories(res.data);
    })
    .catch((err) => {
      console.error("Categories error:", err.response);  // add this
    });

  if (existing) {
    const normalizedDate = existing.expenseDate
    ? new Date(existing.expenseDate).toISOString().split("T")[0]
    : new Date().toISOString().split("T")[0];
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setForm({
      categoryId: existing.categoryId ? String(existing.categoryId) : "",
      amount: existing.amount,
      description: existing.description,
      expenseDate: normalizedDate,
    });
  }
}, [existing]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrors([]);
    try {
        const expenseDate = form.expenseDate
      ? new Date(form.expenseDate).toISOString().split("T")[0]
      : "";
      const payload = {
        categoryId: Number(form.categoryId),
        amount: Number(form.amount),
        description: form.description,
        expenseDate: expenseDate,
      };

      console.log("Sending payload:", payload);

      if (existing) {
        await updateExpense(existing.id, payload);
      } else {
        await createExpense({ ...payload, userId: user.userId });
      }
      onSuccess();
    } catch (err) {
      console.log("Error updating expense:", err);
      const fieldErrors = err.response?.data?.fieldErrors;
      if (fieldErrors) {
        setErrors(fieldErrors.map((e) => e.message));
      } else {
        setErrors([err.response?.data?.message || "Something went wrong"]);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-xl w-full max-w-md p-6">
        <h3 className="text-lg font-semibold text-slate-800 mb-5">
          {existing ? "Edit Expense" : "Add Expense"}
        </h3>

        {errors.length > 0 && (
          <div className="bg-red-50 border border-red-200 rounded-lg px-4 py-3 mb-4">
            {errors.map((e, i) => (
              <p key={i} className="text-red-600 text-sm">• {e}</p>
            ))}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1.5">
              Category
            </label>
            <select
              className="w-full px-3.5 py-2.5 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
              value={form.categoryId}
              onChange={(e) => setForm({ ...form, categoryId: e.target.value })}
              required
            >
              <option value="">Select category</option>
              {categories.map((c) => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1.5">
              Amount (₹)
            </label>
            <input
              type="number"
              step="0.01"
              min="0.01"
              className="w-full px-3.5 py-2.5 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={form.amount}
              onChange={(e) => setForm({ ...form, amount: e.target.value })}
              placeholder="0.00"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1.5">
              Description
            </label>
            <input
              type="text"
              className="w-full px-3.5 py-2.5 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              placeholder="What did you spend on?"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1.5">
              Date
            </label>
            <input
              type="date"
              className="w-full px-3.5 py-2.5 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={form.expenseDate}
              max={new Date().toISOString().split("T")[0]}
              onChange={(e) => setForm({ ...form, expenseDate: e.target.value })}
              required
            />
          </div>

          <div className="flex gap-3 pt-2">
            <button
              type="submit"
              disabled={loading}
              className="flex-1 py-2.5 bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white font-medium rounded-lg text-sm transition-colors"
            >
              {loading ? "Saving..." : existing ? "Update" : "Add Expense"}
            </button>
            <button
              type="button"
              onClick={onCancel}
              className="flex-1 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-700 font-medium rounded-lg text-sm transition-colors"
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}