// src/api/services.js
import api from "./axios";

// ── Auth ──────────────────────────────────────────
export const loginUser = (data) =>
  api.post("/auth/login", data);

export const registerUser = (data) =>
  api.post("/auth/register", data);

// ── Expenses ──────────────────────────────────────
export const getExpenses = (userId, params = {}) =>
  api.get(`/expenses/user/${userId}`, { params });

export const createExpense = (data) =>
  api.post("/expenses", data);

export const updateExpense = (id, data) =>
  api.put(`/expenses/${Number(id)}`, data);

export const deleteExpense = (id) =>
  api.delete(`/expenses/${id}`);

export const getCategorySummary = (userId) =>
  api.get(`/expenses/user/${userId}/summary`);

// ── Ledger ────────────────────────────────────────
export const getLedgerEntries = (userId) =>
  api.get(`/ledger/entries/${userId}`);

export const getLedgerBalance = (userId) =>
  api.get(`/ledger/balance/${userId}`);

// ── Categories ────────────────────────────────────
export const getCategories = () =>
  api.get("/categories");