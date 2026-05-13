'use client';
import { useEffect, useState } from 'react';
import UserSelector from '../../components/UserSelector';
import { createLiability, getLiabilities, Liability } from '../../lib/api';

export default function LiabilitiesPage(){const [userId,setUserId]=useState(''); const [rows,setRows]=useState<Liability[]>([]); const [error,setError]=useState(''); const [form,setForm]=useState<Record<string,string>>({liabilityType:'CREDIT_CARD',name:'',lender:'',currency:'INR',principalAmount:'',outstandingAmount:'',interestRate:'',emiAmount:'',startDate:'',endDate:''});
const load=()=>userId&&getLiabilities(userId).then(setRows).catch(e=>setError((e as Error).message)); useEffect(load,[userId]);
const submit=async()=>{if(!userId)return; await createLiability(userId,{...form,principalAmount:Number(form.principalAmount||0),outstandingAmount:Number(form.outstandingAmount||0),interestRate:Number(form.interestRate||0),emiAmount:Number(form.emiAmount||0)}); load();};
return <main className='container'><h2>Liabilities</h2><UserSelector onChange={setUserId}/>{error&&<p className='warn'>{error}</p>}<table><thead><tr><th>type</th><th>name</th><th>lender</th><th>outstanding</th><th>emi</th></tr></thead><tbody>{rows.map(r=><tr key={r.id}><td>{r.liabilityType}</td><td>{r.name}</td><td>{r.lender}</td><td>{r.outstandingAmount}</td><td>{r.emiAmount}</td></tr>)}</tbody></table><div className='panel'>{Object.keys(form).map(k=><input key={k} placeholder={k} value={form[k]} onChange={e=>setForm({...form,[k]:e.target.value})}/>) }<button onClick={submit}>Add Liability</button></div></main>;}
