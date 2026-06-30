// src/components/ExpenseTable.jsx
export default function ExpenseTable({ expenses, onEdit, onDelete }) {
  if (expenses.length === 0) {
    return (
      <tr>
        <td
          colSpan={5}
          className="text-center py-12 text-slate-400 text-sm"
        >
          No expenses found. Add one to get started.
        </td>
      </tr>
    );
  }

  return expenses.map((exp) => (
    <tr
      key={exp.id}
      className="hover:bg-slate-50 transition-colors"
    >
      <td className="px-5 py-3.5 text-slate-500 text-sm">
        {exp.expenseDate}
      </td>

      <td className="px-5 py-3.5 text-sm">
        <p className="font-medium text-slate-700">{exp.description}</p>
      </td>

      <td className="px-5 py-3.5 text-sm">
        <span className="px-2.5 py-1 bg-blue-50 text-blue-700 rounded-full text-xs font-medium">
          {exp.categoryName}
        </span>
      </td>

      <td className="px-5 py-3.5 text-sm font-semibold text-red-500">
        ₹{Number(exp.amount).toFixed(2)}
      </td>

      <td className="px-5 py-3.5">
        <div className="flex gap-2">
          <button
            onClick={() => 
                {
                    console.log("Editing expense:", exp),
                    onEdit(exp)
                }}
            className="px-3 py-1.5 bg-emerald-50 hover:bg-emerald-100 text-emerald-700 rounded-lg text-xs font-medium transition-colors"
          >
            Edit
          </button>
          <button
            onClick={() => onDelete(exp.id)}
            className="px-3 py-1.5 bg-red-50 hover:bg-red-100 text-red-600 rounded-lg text-xs font-medium transition-colors"
          >
            Delete
          </button>
        </div>
      </td>
    </tr>
  ));
}