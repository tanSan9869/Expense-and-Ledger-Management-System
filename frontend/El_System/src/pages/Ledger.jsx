// src/pages/Ledger.jsx
import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";  
import { useAuth } from "../hooks/useAuth";
import { getLedgerEntries, getLedgerBalance } from "../api/services";
import LedgerTable from "../components/LedgerTable";

export default function Ledger() {
  const { user } = useAuth();
  const location = useLocation();
  const [entries, setEntries] = useState([]);
  const [balance, setBalance] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetch = async () => {
        setLoading(true);
      try {
        const [entRes, balRes] = await Promise.all([
          getLedgerEntries(user.userId),
          getLedgerBalance(user.userId),
        ]);
        setEntries(entRes.data);
        setBalance(balRes.data);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetch();
  }, [user.userId,location.key]);

  if (loading)
    return (
      <div className="flex justify-center items-center h-48">
        <div className="w-8 h-8 border-4 border-blue-500 border-t-transparent rounded-full animate-spin" />
      </div>
    );

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-800">Ledger</h1>
          <p className="text-slate-500 text-sm mt-1">
            Full audit trail of all transactions
          </p>
        </div>
        <div className="bg-white border border-slate-200 rounded-xl px-5 py-3 text-sm">
          <span className="text-slate-500">Balance </span>
          <span
            className={`font-bold text-base ${balance >= 0 ? "text-emerald-600" : "text-red-500"}`}
          >
            ₹{Number(balance).toFixed(2)}
          </span>
        </div>
      </div>

      {/* Ledger table */}
      <div className="bg-white border border-slate-200 rounded-2xl overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-slate-50 border-b border-slate-200">
              {[
                "Date",
                "Description",
                "Type",
                "Debit",
                "Credit",
                "Running Balance",
              ].map((h) => (
                <th
                  key={h}
                  className="text-left px-5 py-3.5 text-xs font-semibold text-slate-500 uppercase tracking-wider"
                >
                  {h}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            <LedgerTable entries={entries} />
          </tbody>
        </table>
      </div>
    </div>
  );
}
