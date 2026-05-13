'use client';

import { useEffect, useState } from 'react';
import UserSelector from '../../components/UserSelector';
import { createLiability, getLiabilities, Liability } from '../../lib/api';

type FormState = Record<string, string>;

function sanitizeLiabilityPayload(form: FormState) {
  const payload: Record<string, string | number> = {};
  Object.entries(form).forEach(([key, value]) => {
    const trimmed = value.trim();
    if (trimmed === '') return;
    if (['principalAmount', 'outstandingAmount', 'interestRate', 'emiAmount'].includes(key)) {
      payload[key] = Number(trimmed);
      return;
    }
    if (key === 'startDate' || key === 'endDate') {
      payload[key] = trimmed;
      return;
    }
    payload[key] = trimmed;
  });
  return payload;
}

export default function LiabilitiesPage() {
  const [userId, setUserId] = useState('');
  const [rows, setRows] = useState<Liability[]>([]);
  const [error, setError] = useState('');
  const [form, setForm] = useState<FormState>({
    liabilityType: 'CREDIT_CARD',
    name: '',
    lender: '',
    currency: 'INR',
    principalAmount: '',
    outstandingAmount: '',
    interestRate: '',
    emiAmount: '',
    startDate: '',
    endDate: ''
  });

  const load = () => {
    if (!userId) return;
    getLiabilities(userId).then(setRows).catch((e) => setError((e as Error).message));
  };

  useEffect(load, [userId]);

  const submit = async () => {
    if (!userId) return;
    try {
      setError('');
      await createLiability(userId, sanitizeLiabilityPayload(form));
      load();
    } catch (e) {
      setError((e as Error).message);
    }
  };

  return (
    <main className="container">
      <h2>Liabilities</h2>
      <UserSelector onChange={setUserId} />
      {error && <p className="warn">{error}</p>}
      <table>
        <thead><tr><th>type</th><th>name</th><th>lender</th><th>outstanding</th><th>emi</th></tr></thead>
        <tbody>{rows.map((row) => <tr key={row.id}><td>{row.liabilityType}</td><td>{row.name}</td><td>{row.lender}</td><td>{row.outstandingAmount}</td><td>{row.emiAmount}</td></tr>)}</tbody>
      </table>
      <div className="panel">
        {Object.keys(form).map((key) => <input key={key} placeholder={key} value={form[key]} onChange={(event) => setForm({ ...form, [key]: event.target.value })} />)}
        <button onClick={submit}>Add Liability</button>
      </div>
    </main>
  );
}
