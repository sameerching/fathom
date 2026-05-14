'use client';
import Link from 'next/link';
import { useCallback, useState } from 'react';
import AppShell from '../../components/AppShell';
import EmptyState from '../../components/EmptyState';
import ErrorMessage from '../../components/ErrorMessage';
import LoadingMessage from '../../components/LoadingMessage';
import StatCard from '../../components/StatCard';
import UserSelector from '../../components/UserSelector';
import { formatCurrency, formatPercent } from '../../lib/format';
import { getBudgetSummary, getCategoryBreakdown, getMonthlySummary, getNetWorth } from '../../lib/api';
function getMonthDateRange(month: string) { const [y, m] = month.split('-'); const d = new Date(Number(y), Number(m), 0).getDate(); return { from: `${month}-01`, to: `${month}-${String(d).padStart(2, '0')}` }; }
export default function DashboardPage() {
  const [userId, setUserId] = useState(''); const [month, setMonth] = useState(new Date().toISOString().slice(0, 7));
  const [data, setData] = useState<Record<string, string | number> | null>(null); const [breakdown, setBreakdown] = useState<any[]>([]); const [budgetSummary, setBudgetSummary] = useState<any[]>([]);
  const [error, setError] = useState(''); const [loading, setLoading] = useState(false);
  const load = useCallback(async () => { if (!userId) return; const range = getMonthDateRange(month); setLoading(true); setError(''); try { const [summary, categoryBreakdown, netWorth, budgets] = await Promise.all([getMonthlySummary(userId, month), getCategoryBreakdown(userId, range.from, range.to, 'EXPENSE'), getNetWorth(userId), getBudgetSummary(userId, month)]); setData({ ...summary, ...netWorth }); setBreakdown(categoryBreakdown); setBudgetSummary(budgets);} catch (e) { setError((e as Error).message); } finally { setLoading(false); } }, [month, userId]);
  return <AppShell><h2>Dashboard</h2><p><a href='/planning'>Open Monthly Planning</a></p><UserSelector onChange={setUserId} /><div className="panel"><label>Month</label><input type="month" value={month} onChange={(event) => setMonth(event.target.value)} /><button onClick={load}>Refresh Dashboard</button></div>{loading && <LoadingMessage message="Loading dashboard..." />}<ErrorMessage message={error} />{data && <div className="grid">{[{k:'income',f:formatCurrency},{k:'expenses',f:formatCurrency},{k:'investments',f:formatCurrency},{k:'liabilityPayments',f:formatCurrency},{k:'netCashFlow',f:formatCurrency},{k:'savingsRate',f:formatPercent},{k:'totalAssets',f:formatCurrency},{k:'totalLiabilities',f:formatCurrency},{k:'netWorth',f:formatCurrency}].map(({k,f})=><StatCard key={k} label={k} value={f(data[k] as number)} />)}</div>}<h3>Category Breakdown</h3>{breakdown.length===0?<EmptyState title="No category rows yet" description="No transactions found for this month and filter." action={<Link href="/upload">Go to Upload</Link>} />:<table><thead><tr><th>Category</th><th>Amount</th><th>Count</th></tr></thead><tbody>{breakdown.map((row, index) => <tr key={`${row.categoryName}-${index}`}><td>{row.categoryName}</td><td>{formatCurrency(row.amount)}</td><td>{row.transactionCount}</td></tr>)}</tbody></table>}<h3>Budget Snapshot</h3><p><Link href='/budgets'>Go to Budgets</Link></p>{budgetSummary.length===0?<EmptyState title='No budgets for this month' description='Create monthly budgets to track spend.'/>
:<table><thead><tr><th>Name</th><th>Budget</th><th>Actual</th><th>Status</th></tr></thead><tbody>{budgetSummary.map((b:any)=><tr key={b.budgetId} style={{background:b.status==='OVER_BUDGET'?'#fff1f1':'transparent'}}><td>{b.name}</td><td>{formatCurrency(b.budgetAmount)}</td><td>{formatCurrency(b.actualAmount)}</td><td>{b.status}</td></tr>)}</tbody></table>}</AppShell>;
}
