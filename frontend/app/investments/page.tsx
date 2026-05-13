'use client';
import { useEffect, useState } from 'react';
import UserSelector from '../../components/UserSelector';
import { createInvestmentHolding, getInvestmentHoldings, InvestmentHolding } from '../../lib/api';

export default function InvestmentsPage(){const [userId,setUserId]=useState(''); const [rows,setRows]=useState<InvestmentHolding[]>([]); const [error,setError]=useState(''); const [form,setForm]=useState<Record<string,string>>({assetType:'MUTUAL_FUND',name:'',provider:'',symbol:'',currency:'INR',investedAmount:'',currentValue:'',asOfDate:''});
const load=()=>userId&&getInvestmentHoldings(userId).then(setRows).catch(e=>setError((e as Error).message)); useEffect(load,[userId]);
const submit=async()=>{if(!userId)return; await createInvestmentHolding(userId,{...form,investedAmount:Number(form.investedAmount||0),currentValue:Number(form.currentValue||0)}); load();};
return <main className='container'><h2>Investments</h2><UserSelector onChange={setUserId}/>{error&&<p className='warn'>{error}</p>}<table><thead><tr><th>type</th><th>name</th><th>provider</th><th>symbol</th><th>currentValue</th></tr></thead><tbody>{rows.map(r=><tr key={r.id}><td>{r.assetType}</td><td>{r.name}</td><td>{r.provider}</td><td>{r.symbol}</td><td>{r.currentValue}</td></tr>)}</tbody></table><div className='panel'>{Object.keys(form).map(k=><input key={k} placeholder={k} value={form[k]} onChange={e=>setForm({...form,[k]:e.target.value})}/>) }<button onClick={submit}>Add Investment</button></div></main>;}
