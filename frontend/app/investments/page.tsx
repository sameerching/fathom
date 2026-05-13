'use client';

import { useEffect, useState } from 'react';
import UserSelector from '../../components/UserSelector';
import { createInvestmentHolding, getInvestmentHoldings, InvestmentHolding } from '../../lib/api';

type FormState = Record<string, string>;

function sanitizeInvestmentPayload(form: FormState) {
  const payload: Record<string, string | number> = {};
  Object.entries(form).forEach(([key, value]) => {
    const trimmed = value.trim();
    if (trimmed === '') return;
    if (key === 'investedAmount' || key === 'currentValue') {
      payload[key] = Number(trimmed);
      return;
    }
    if (key === 'asOfDate') {
      payload[key] = trimmed;
      return;
    }
    payload[key] = trimmed;
  });
  return payload;
}

export default function InvestmentsPage() {
  const [userId, setUserId] = useState('');
  const [rows, setRows] = useState<InvestmentHolding[]>([]);
  const [error, setError] = useState('');
  const [form, setForm] = useState<FormState>({
    assetType: 'MUTUAL_FUND',
    name: '',
    provider: '',
    symbol: '',
    currency: 'INR',
    investedAmount: '',
    currentValue: '',
    asOfDate: ''
  });

  const load = () => {
    if (!userId) return;
    getInvestmentHoldings(userId).then(setRows).catch((e) => setError((e as Error).message));
  };

  useEffect(load, [userId]);

  const submit = async () => {
    if (!userId) return;
    try {
      setError('');
      await createInvestmentHolding(userId, sanitizeInvestmentPayload(form));
      load();
    } catch (e) {
      setError((e as Error).message);
    }
  };

  return (
    <main className="container">
      <h2>Investments</h2>
      <UserSelector onChange={setUserId} />
      {error && <p className="warn">{error}</p>}
      <table>
        <thead><tr><th>type</th><th>name</th><th>provider</th><th>symbol</th><th>currentValue</th></tr></thead>
        <tbody>{rows.map((row) => <tr key={row.id}><td>{row.assetType}</td><td>{row.name}</td><td>{row.provider}</td><td>{row.symbol}</td><td>{row.currentValue}</td></tr>)}</tbody>
      </table>
      <div className="panel">
        {Object.keys(form).map((key) => <input key={key} placeholder={key} value={form[key]} onChange={(event) => setForm({ ...form, [key]: event.target.value })} />)}
        <button onClick={submit}>Add Investment</button>
      </div>
    </main>
  );
}
