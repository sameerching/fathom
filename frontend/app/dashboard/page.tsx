'use client';

import { useCallback, useState } from 'react';
import UserSelector from '../../components/UserSelector';
import { getCategoryBreakdown, getMonthlySummary, getNetWorth } from '../../lib/api';

function getMonthDateRange(month: string) {
  const [yearString, monthString] = month.split('-');
  const year = Number(yearString);
  const monthIndex = Number(monthString) - 1;
  const lastDay = new Date(year, monthIndex + 1, 0).getDate();
  const paddedLastDay = String(lastDay).padStart(2, '0');
  return {
    from: `${month}-01`,
    to: `${month}-${paddedLastDay}`
  };
}

export default function DashboardPage() {
  const [userId, setUserId] = useState('');
  const [month, setMonth] = useState(new Date().toISOString().slice(0, 7));
  const [data, setData] = useState<Record<string, string | number> | null>(null);
  const [breakdown, setBreakdown] = useState<Array<{ categoryName: string; amount: number; transactionCount: number }>>([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const load = useCallback(async () => {
    if (!userId) return;
    const range = getMonthDateRange(month);

    setLoading(true);
    setError('');
    try {
      const [summary, categoryBreakdown, netWorth] = await Promise.all([
        getMonthlySummary(userId, month),
        getCategoryBreakdown(userId, range.from, range.to, 'EXPENSE'),
        getNetWorth(userId)
      ]);
      setData({ ...summary, ...netWorth });
      setBreakdown(categoryBreakdown);
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setLoading(false);
    }
  }, [month, userId]);

  return (
    <main className="container">
      <h2>Dashboard</h2>
      <UserSelector onChange={setUserId} />
      <div className="panel">
        <label>Month</label>
        <input type="month" value={month} onChange={(event) => setMonth(event.target.value)} />
        <button onClick={load}>Load Dashboard</button>
      </div>

      {loading && <p>Loading...</p>}
      {error && <p className="warn">{error}</p>}

      {data && (
        <div className="grid">
          {['income', 'expenses', 'investments', 'liabilityPayments', 'netCashFlow', 'savingsRate', 'totalAssets', 'totalLiabilities', 'netWorth'].map((key) => (
            <div key={key} className="card">
              <strong>{key}</strong>
              <div>{String(data[key] ?? '-')}</div>
            </div>
          ))}
        </div>
      )}

      <h3>Category Breakdown</h3>
      <table>
        <thead>
          <tr>
            <th>Category</th>
            <th>Amount</th>
            <th>Count</th>
          </tr>
        </thead>
        <tbody>
          {breakdown.map((row, index) => (
            <tr key={`${row.categoryName}-${index}`}>
              <td>{row.categoryName}</td>
              <td>{row.amount}</td>
              <td>{row.transactionCount}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </main>
  );
}
