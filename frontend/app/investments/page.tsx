'use client';
import { useEffect, useState } from 'react';
import AppShell from '../../components/AppShell';
import ErrorMessage from '../../components/ErrorMessage';
import FormField from '../../components/FormField';
import LoadingMessage from '../../components/LoadingMessage';
import UserSelector from '../../components/UserSelector';
import { formatCurrency } from '../../lib/format';
import { createInvestmentHolding, getInvestmentHoldings, InvestmentHolding } from '../../lib/api';

type FormState = Record<string, string>;
const assetTypes = ['MUTUAL_FUND','STOCK','ETF','FD','GOLD','RSU','EPF','PPF','NPS','CASH','OTHER'];
function sanitize(form: FormState){const payload:Record<string,string|number>={}; Object.entries(form).forEach(([k,v])=>{const t=v.trim(); if(t==='')return; if(k==='investedAmount'||k==='currentValue'){payload[k]=Number(t); return;} payload[k]=t;}); return payload;}
export default function InvestmentsPage(){const [userId,setUserId]=useState(''); const [rows,setRows]=useState<InvestmentHolding[]>([]); const [error,setError]=useState(''); const [loading,setLoading]=useState(false); const [form,setForm]=useState<FormState>({assetType:'MUTUAL_FUND',name:'',provider:'',symbol:'',currency:'INR',investedAmount:'',currentValue:'',asOfDate:''});
const load=()=>{if(!userId) return; setLoading(true); getInvestmentHoldings(userId).then(setRows).catch(e=>setError((e as Error).message)).finally(()=>setLoading(false));}; useEffect(load,[userId]);
const submit=async()=>{if(!userId)return; try{setError(''); setLoading(true); await createInvestmentHolding(userId,sanitize(form)); load();}catch(e){setError((e as Error).message);}finally{setLoading(false);}};
return <AppShell><h2>Investments</h2><UserSelector onChange={setUserId} />{loading&&<LoadingMessage />}<ErrorMessage message={error} /><table><thead><tr><th>type</th><th>name</th><th>provider</th><th>symbol</th><th>investedAmount</th><th>currentValue</th></tr></thead><tbody>{rows.map((r)=><tr key={r.id}><td>{r.assetType}</td><td>{r.name}</td><td>{r.provider}</td><td>{r.symbol}</td><td>{formatCurrency(r.investedAmount)}</td><td>{formatCurrency(r.currentValue)}</td></tr>)}</tbody></table><div className='panel'><FormField label='Asset type'><select value={form.assetType} onChange={(e)=>setForm({...form,assetType:e.target.value})}>{assetTypes.map((a)=><option key={a} value={a}>{a}</option>)}</select></FormField><FormField label='Name'><input value={form.name} onChange={(e)=>setForm({...form,name:e.target.value})} /></FormField><FormField label='Provider'><input value={form.provider} onChange={(e)=>setForm({...form,provider:e.target.value})} /></FormField><FormField label='Symbol'><input value={form.symbol} onChange={(e)=>setForm({...form,symbol:e.target.value})} /></FormField><button onClick={submit} disabled={!userId || !form.assetType || !form.name || loading}>Add Investment</button></div></AppShell>}
