'use client';

import { useCallback, useEffect, useState } from 'react';
import AppShell from './AppShell';
import UserSelector from './UserSelector';
import { formatCurrency } from '../lib/format';
import { Account, Category, CreateRecurringTransactionRequest, createRecurringTransaction, deactivateRecurringTransaction, getMonthlyPlanningSummary, getRecurringTransactions, getSystemCategories, getUserAccounts, getUserCategories, MonthlyPlanningSummary, RecurringTransaction } from '../lib/api';

type FormState = { name: string; amount: string; direction: string; transactionType: string; frequency: string; dayOfMonth: string; startDate: string; endDate: string; accountId: string; categoryId: string; notes: string; active: boolean };

const initialForm = (): FormState => ({ name: '', amount: '', direction: 'DEBIT', transactionType: 'EXPENSE', frequency: 'MONTHLY', dayOfMonth: '', startDate: new Date().toISOString().slice(0, 10), endDate: '', accountId: '', categoryId: '', notes: '', active: true });

export default function PlanningPage() {
  const [userId, setUserId] = useState('');
  const [month, setMonth] = useState(new Date().toISOString().slice(0, 7));
  const [summary, setSummary] = useState<MonthlyPlanningSummary | null>(null);
  const [recurring, setRecurring] = useState<RecurringTransaction[]>([]);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [form, setForm] = useState<FormState>(initialForm());
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    if (!userId) return;
    setError('');
    try {
      const [planning, recurringRows, userAccounts, systemCategories, userCategories] = await Promise.all([
        getMonthlyPlanningSummary(userId, month),
        getRecurringTransactions(userId),
        getUserAccounts(userId),
        getSystemCategories(),
        getUserCategories(userId)
      ]);
      setSummary(planning);
      setRecurring(recurringRows);
      setAccounts(userAccounts);
      setCategories([...systemCategories, ...userCategories]);
    } catch (e) {
      setError((e as Error).message);
    }
  }, [month, userId]);

  useEffect(() => { load(); }, [load]);

  const submit = async () => {
    if (!userId) return;
    const payload: CreateRecurringTransactionRequest = {
      name: form.name.trim(),
      amount: Number(form.amount),
      direction: form.direction,
      transactionType: form.transactionType,
      frequency: form.frequency,
      startDate: form.startDate,
      active: form.active
    };
    if (form.dayOfMonth.trim()) payload.dayOfMonth = Number(form.dayOfMonth);
    if (form.endDate.trim()) payload.endDate = form.endDate;
    if (form.accountId.trim()) payload.accountId = form.accountId;
    if (form.categoryId.trim()) payload.categoryId = form.categoryId;
    if (form.notes.trim()) payload.notes = form.notes.trim();

    await createRecurringTransaction(userId, payload);
    setForm(initialForm());
    await load();
  };

  return <AppShell><h2>Planning</h2><UserSelector onChange={setUserId} />
    <div className='panel'><label>Month</label><input type='month' value={month} onChange={(e) => setMonth(e.target.value)} /><button onClick={load}>Load Planning Summary</button></div>
    {error && <p>{error}</p>}
    {summary && <table><thead><tr><th>Metric</th><th>Planned</th><th>Actual</th><th>Variance</th></tr></thead><tbody>{[
      ['Income', 'plannedIncome', 'actualIncome', 'incomeVariance'], ['Expenses', 'plannedExpenses', 'actualExpenses', 'expensesVariance'], ['Investments', 'plannedInvestments', 'actualInvestments', 'investmentsVariance'], ['Liability Payments', 'plannedLiabilityPayments', 'actualLiabilityPayments', 'liabilityPaymentsVariance'], ['Net Cash Flow', 'plannedNetCashFlow', 'actualNetCashFlow', 'netCashFlowVariance']
    ].map(([name, p, a, v]) => <tr key={String(name)}><td>{name}</td><td>{formatCurrency(summary[p as keyof MonthlyPlanningSummary] as number)}</td><td>{formatCurrency(summary[a as keyof MonthlyPlanningSummary] as number)}</td><td>{formatCurrency(summary[v as keyof MonthlyPlanningSummary] as number)}</td></tr>)}</tbody></table>}
    <h3>Create Recurring Template</h3>
    <div className='panel'>
      <input placeholder='Name' value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
      <input placeholder='Amount' type='number' value={form.amount} onChange={(e) => setForm({ ...form, amount: e.target.value })} />
      <select value={form.direction} onChange={(e) => setForm({ ...form, direction: e.target.value })}><option>CREDIT</option><option>DEBIT</option></select>
      <select value={form.transactionType} onChange={(e) => setForm({ ...form, transactionType: e.target.value })}>{['INCOME', 'EXPENSE', 'INVESTMENT', 'LIABILITY_PAYMENT', 'TRANSFER', 'REFUND', 'ADJUSTMENT'].map((v) => <option key={v}>{v}</option>)}</select>
      <select value={form.frequency} onChange={(e) => setForm({ ...form, frequency: e.target.value })}><option>MONTHLY</option><option>QUARTERLY</option><option>YEARLY</option></select>
      <input placeholder='Day of month (optional)' type='number' value={form.dayOfMonth} onChange={(e) => setForm({ ...form, dayOfMonth: e.target.value })} />
      <input type='date' value={form.startDate} onChange={(e) => setForm({ ...form, startDate: e.target.value })} />
      <input type='date' value={form.endDate} onChange={(e) => setForm({ ...form, endDate: e.target.value })} />
      <select value={form.accountId} onChange={(e) => setForm({ ...form, accountId: e.target.value })}><option value=''>No account</option>{accounts.map((a) => <option key={a.id} value={a.id}>{a.name}</option>)}</select>
      <select value={form.categoryId} onChange={(e) => setForm({ ...form, categoryId: e.target.value })}><option value=''>No category</option>{categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}</select>
      <textarea placeholder='Notes (optional)' value={form.notes} onChange={(e) => setForm({ ...form, notes: e.target.value })} />
      <button onClick={submit}>Create Recurring Transaction</button>
    </div>
    <h3>Recurring Templates</h3>
    <table><thead><tr><th>Name</th><th>Amount</th><th>Direction</th><th>Type</th><th>Frequency</th><th>Day</th><th>Start</th><th>End</th><th>Active</th><th>Action</th></tr></thead>
      <tbody>{recurring.map((x) => <tr key={x.id}><td>{x.name}</td><td>{formatCurrency(x.amount)}</td><td>{x.direction}</td><td>{x.transactionType}</td><td>{x.frequency}</td><td>{x.dayOfMonth ?? '-'}</td><td>{x.startDate}</td><td>{x.endDate ?? '-'}</td><td>{String(x.active)}</td><td>{x.active ? <button onClick={async () => { await deactivateRecurringTransaction(x.id); await load(); }}>Deactivate</button> : '-'}</td></tr>)}</tbody></table>
  </AppShell>;
}
