// src/components/CategorySummary.jsx
const COLORS = [
  "bg-blue-500", "bg-violet-500", "bg-amber-500",
  "bg-emerald-500", "bg-rose-500", "bg-cyan-500",
];

export default function CategorySummary({ data }) {
  if (!data || data.length === 0)
    return <p className="text-slate-400 text-sm text-center py-8">No data yet.</p>;

  const max = Math.max(...data.map((d) => d.totalAmount));

  return (
    <div className="space-y-4">
      {data.map((item, i) => (
        <div key={item.categoryName}>
          <div className="flex justify-between items-center mb-1.5">
            <div className="flex items-center gap-2">
              <span className={`w-2 h-2 rounded-full ${COLORS[i % COLORS.length]}`} />
              <span className="text-sm text-slate-600">{item.categoryName}</span>
            </div>
            <span className="text-sm font-medium text-slate-800">
              ₹{Number(item.totalAmount).toFixed(2)}
            </span>
          </div>
          <div className="h-1.5 bg-slate-100 rounded-full overflow-hidden">
            <div
              className={`h-full rounded-full transition-all duration-500 ${COLORS[i % COLORS.length]}`}
              style={{ width: `${(item.totalAmount / max) * 100}%` }}
            />
          </div>
        </div>
      ))}
    </div>
  );
}