// src/components/LedgerTable.jsx
export default function LedgerTable({ entries }) {
  if (entries.length === 0) {
    return (
      <tr>
        <td
          colSpan={6}
          className="text-center py-12 text-slate-400 text-sm"
        >
          No ledger entries yet. Add an expense to get started.
        </td>
      </tr>
    );
  }

  return entries.map((entry) => (
    <tr
      key={entry.id}
      className="hover:bg-slate-50 transition-colors"
    >
      <td className="px-5 py-3.5 text-slate-500 text-sm">
        {new Date(entry.entryDate).toLocaleDateString("en-IN", {
          day: "2-digit",
          month: "short",
          year: "numeric",
        })}
      </td>

      <td className="px-5 py-3.5 text-sm font-medium text-slate-700">
        {entry.expenseDescription}
      </td>

      <td className="px-5 py-3.5 text-sm">
        <span
          className={`px-2.5 py-1 rounded-full text-xs font-semibold ${
            entry.entryType === "DEBIT"
              ? "bg-red-50 text-red-600"
              : "bg-emerald-50 text-emerald-700"
          }`}
        >
          {entry.entryType}
        </span>
      </td>

      <td className="px-5 py-3.5 text-sm font-medium text-red-500">
        {entry.debitAmount > 0
          ? `₹${Number(entry.debitAmount).toFixed(2)}`
          : <span className="text-slate-300">—</span>
        }
      </td>

      <td className="px-5 py-3.5 text-sm font-medium text-emerald-600">
        {entry.creditAmount > 0
          ? `₹${Number(entry.creditAmount).toFixed(2)}`
          : <span className="text-slate-300">—</span>
        }
      </td>

      <td className="px-5 py-3.5 text-sm font-semibold text-slate-800">
        <span
          className={
            Number(entry.runningBalance) >= 0
              ? "text-slate-800"
              : "text-red-500"
          }
        >
          ₹{Number(entry.runningBalance).toFixed(2)}
        </span>
      </td>
    </tr>
  ));
}