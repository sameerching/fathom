'use client';
import { useState } from 'react';
import UserSelector from '../../components/UserSelector';
import { getTransactions, Transaction } from '../../lib/api';

export default function TransactionsPage(){const [userId,setUserId]=useState(''); const [rows,setRows]=useState<Transaction[]>([]); const [error,setError]=useState(''); const [filters,setFilters]=useState<Record<string,string>>({from:'',to:'',merchant:'',transactionType:'',direction:'',minAmount:'',maxAmount:''});
const load=async()=>{if(!userId)return; try{setError(''); setRows(await getTransactions(userId,filters));}catch(e){setError((e as Error).message);}};
return <main className='container'><h2>Transactions</h2><UserSelector onChange={setUserId}/><div className='panel'>{Object.keys(filters).map(k=><input key={k} placeholder={k} value={filters[k]} onChange={e=>setFilters({...filters,[k]:e.target.value})}/>) }<button onClick={load}>Apply filters</button></div>{error&&<p className='warn'>{error}</p>}<table><thead><tr><th>date</th><th>direction</th><th>amount</th><th>type</th><th>merchant</th><th>rawDescription</th><th>accountId</th><th>categoryId</th></tr></thead><tbody>{rows.map(r=><tr key={r.id}><td>{r.transactionDate}</td><td>{r.direction}</td><td>{r.amount}</td><td>{r.transactionType}</td><td>{r.merchant}</td><td>{r.rawDescription}</td><td>{r.accountId}</td><td>{r.categoryId}</td></tr>)}</tbody></table></main>;}
