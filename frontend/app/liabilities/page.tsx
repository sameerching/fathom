'use client';
import { useEffect, useState } from 'react';
import AppShell from '../../components/AppShell';
import ErrorMessage from '../../components/ErrorMessage';
import FormField from '../../components/FormField';
import LoadingMessage from '../../components/LoadingMessage';
import UserSelector from '../../components/UserSelector';
import { formatCurrency } from '../../lib/format';
import { createLiability, getLiabilities, Liability } from '../../lib/api';

type FormState = Record<string, string>;
const liabilityTypes = ['HOME_LOAN','PERSONAL_LOAN','CREDIT_CARD','VEHICLE_LOAN','EDUCATION_LOAN','OTHER'];
function sanitize(form: FormState){const payload:Record<string,string|number>={}; Object.entries(form).forEach(([k,v])=>{const t=v.trim(); if(t==='')return; if(['principalAmount','outstandingAmount','interestRate','emiAmount'].includes(k)){payload[k]=Number(t); return;} payload[k]=t;}); return payload;}
export default function LiabilitiesPage(){const [userId,setUserId]=useState(''); const [rows,setRows]=useState<Liability[]>([]); const [error,setError]=useState(''); const [loading,setLoading]=useState(false); const [form,setForm]=useState<FormState>({liabilityType:'CREDIT_CARD',name:'',lender:'',currency:'INR',principalAmount:'',outstandingAmount:'',interestRate:'',emiAmount:'',startDate:'',endDate:''});
const load=()=>{if(!userId) return; setLoading(true); getLiabilities(userId).then(setRows).catch(e=>setError((e as Error).message)).finally(()=>setLoading(false));}; useEffect(load,[userId]);
const submit=async()=>{if(!userId)return; try{setError(''); setLoading(true); await createLiability(userId,sanitize(form)); load();}catch(e){setError((e as Error).message);}finally{setLoading(false);}};
return <AppShell><h2>Liabilities</h2><UserSelector onChange={setUserId} />{loading&&<LoadingMessage />}<ErrorMessage message={error} /><table><thead><tr><th>type</th><th>name</th><th>lender</th><th>outstanding</th><th>principal</th><th>emi</th></tr></thead><tbody>{rows.map((r)=><tr key={r.id}><td>{r.liabilityType}</td><td>{r.name}</td><td>{r.lender}</td><td>{formatCurrency(r.outstandingAmount)}</td><td>{formatCurrency(r.principalAmount)}</td><td>{formatCurrency(r.emiAmount)}</td></tr>)}</tbody></table><div className='panel'><FormField label='Liability type'><select value={form.liabilityType} onChange={(e)=>setForm({...form,liabilityType:e.target.value})}>{liabilityTypes.map((t)=><option key={t} value={t}>{t}</option>)}</select></FormField><FormField label='Name'><input value={form.name} onChange={(e)=>setForm({...form,name:e.target.value})} /></FormField><FormField label='Outstanding amount'><input value={form.outstandingAmount} onChange={(e)=>setForm({...form,outstandingAmount:e.target.value})} /></FormField><button onClick={submit} disabled={!userId || !form.liabilityType || !form.name || !form.outstandingAmount || loading}>Add Liability</button></div></AppShell>}
