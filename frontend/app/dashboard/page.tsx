'use client';
import { useCallback, useMemo, useState } from 'react';
import UserSelector from '../../components/UserSelector';
import { getCategoryBreakdown, getMonthlySummary, getNetWorth } from '../../lib/api';

export default function DashboardPage() {
  const [userId, setUserId] = useState(''); const [month, setMonth] = useState(new Date().toISOString().slice(0, 7));
  const [data, setData] = useState<any>(null); const [breakdown, setBreakdown] = useState<any[]>([]); const [error, setError] = useState(''); const [loading, setLoading] = useState(false);
  const dates = useMemo(() => { const [y,m]=month.split('-').map(Number); const last = new Date(y,m,0).toISOString().slice(0,10); return {from:`${month}-01`,to:last}; }, [month]);
  const load = useCallback(async () => { if(!userId) return; setLoading(true); setError(''); try { const [s,b,n] = await Promise.all([getMonthlySummary(userId,month),getCategoryBreakdown(userId,dates.from,dates.to,'EXPENSE'),getNetWorth(userId)]); setData({...s,...n}); setBreakdown(b);} catch(e){setError((e as Error).message);} finally{setLoading(false);} }, [userId,month,dates.from,dates.to]);
  return <main className='container'><h2>Dashboard</h2><UserSelector onChange={setUserId}/><div className='panel'><label>Month</label><input type='month' value={month} onChange={e=>setMonth(e.target.value)}/><button onClick={load}>Load Dashboard</button></div>{loading&&<p>Loading...</p>}{error&&<p className='warn'>{error}</p>}{data&&<div className='grid'>{['income','expenses','investments','liabilityPayments','netCashFlow','savingsRate','totalAssets','totalLiabilities','netWorth'].map(k=><div key={k} className='card'><strong>{k}</strong><div>{String(data[k] ?? '-')}</div></div>)}</div>}<h3>Category Breakdown</h3><table><thead><tr><th>Category</th><th>Amount</th><th>Count</th></tr></thead><tbody>{breakdown.map((r,i)=><tr key={i}><td>{r.categoryName}</td><td>{r.amount}</td><td>{r.transactionCount}</td></tr>)}</tbody></table></main>;
}
