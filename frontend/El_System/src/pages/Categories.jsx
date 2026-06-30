// src/pages/Categories.jsx
import { useEffect, useState } from "react";
import { getCategories } from "../api/services";
import api from "../api/axios";

export default function Categories() {
  const [categories, setCategories] = useState([]);
  const [form, setForm] = useState({ name: "", description: "" });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const fetchCategories = async () => {
    const res = await getCategories();
    setCategories(res.data);
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchCategories();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setSuccess("");
    try {
      await api.post("/categories", form);
      setSuccess(`Category "${form.name}" added successfully.`);
      setForm({ name: "", description: "" });
      fetchCategories();
    } catch (err) {
      setError(err.response?.data?.message || "Failed to add category");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-semibold text-slate-800">Categories</h1>
        <p className="text-slate-500 text-sm mt-1">
          Manage expense categories for your account
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">

        {/* Add category form */}
        <div className="bg-white border border-slate-200 rounded-2xl p-6">
          <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-widest mb-5">
            Add New Category
          </h2>

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 text-sm px-4 py-3 rounded-lg mb-4">
              {error}
            </div>
          )}

          {success && (
            <div className="bg-emerald-50 border border-emerald-200 text-emerald-700 text-sm px-4 py-3 rounded-lg mb-4">
              {success}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">
                Category Name
              </label>
              <input
                type="text"
                className="w-full px-3.5 py-2.5 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
                placeholder="e.g. Food & Drinks"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">
                Description
                <span className="text-slate-400 font-normal">(optional)</span>
              </label>
              <input
                type="text"
                className="w-full px-3.5 py-2.5 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                value={form.description}
                onChange={(e) => setForm({ ...form, description: e.target.value })}
                placeholder="Short description"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full py-2.5 bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white font-medium rounded-lg text-sm transition-colors"
            >
              {loading ? "Adding..." : "Add Category"}
            </button>
          </form>
        </div>

        {/* Existing categories list */}
        <div className="bg-white border border-slate-200 rounded-2xl p-6">
          <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-widest mb-5">
            Existing Categories ({categories.length})
          </h2>

          {categories.length === 0 ? (
            <p className="text-slate-400 text-sm text-center py-8">
              No categories yet. Add one on the left.
            </p>
          ) : (
            <div className="space-y-2">
              {categories.map((cat) => (
                <div
                  key={cat.id}
                  className="flex items-start gap-3 px-4 py-3 bg-slate-50 rounded-xl"
                >
                  <div className="w-2 h-2 rounded-full bg-blue-500 mt-1.5 shrink-0" />
                  <div>
                    <p className="text-sm font-medium text-slate-700">
                      {cat.name}
                    </p>
                    {cat.description && (
                      <p className="text-xs text-slate-400 mt-0.5">
                        {cat.description}
                      </p>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}