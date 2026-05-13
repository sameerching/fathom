'use client';
import { useEffect, useState } from 'react';
import AppShell from '../../components/AppShell';
import ErrorMessage from '../../components/ErrorMessage';
import LoadingMessage from '../../components/LoadingMessage';
import StatCard from '../../components/StatCard';
import UserSelector from '../../components/UserSelector';
import { Account, getUserAccounts, ImportSummary, uploadTransactionsCsv } from '../../lib/api';

export default function UploadPage(){const [userId,setUserId]=useState(''); const [accounts,setAccounts]=useState<Account[]>([]); const [accountId,setAccountId]=useState(''); const [source,setSource]=useState('MANUAL'); const [file,setFile]=useState<File|null>(null); const [result,setResult]=useState<ImportSummary|null>(null); const [error,setError]=useState(''); const [loading,setLoading]=useState(false);
useEffect(()=>{if(!userId)return; getUserAccounts(userId).then(setAccounts).catch(e=>setError((e as Error).message));},[userId]);
const upload=async()=>{if(!userId||!accountId||!file)return; try{setLoading(true);setError(''); setResult(await uploadTransactionsCsv(userId,accountId,file,source));}catch(e){setError((e as Error).message);}finally{setLoading(false);}};
return <AppShell><h2>Upload CSV</h2><UserSelector onChange={setUserId}/><div className='panel'><p><strong>Required headers:</strong> transactionDate, direction, amount, rawDescription, transactionType</p><pre>transactionDate,direction,amount,rawDescription,merchant,transactionType,categoryName,notes
2026-05-01,DEBIT,1250.00,Swiggy order,Swiggy,EXPENSE,Food,Dinner</pre><select value={accountId} onChange={e=>setAccountId(e.target.value)}><option value=''>Select account</option>{accounts.map(a=><option key={a.id} value={a.id}>{a.name}</option>)}</select><select value={source} onChange={e=>setSource(e.target.value)}>{['MANUAL','BANK_STATEMENT','CREDIT_CARD_STATEMENT'].map(s=><option key={s}>{s}</option>)}</select><input type='file' accept='.csv' onChange={e=>setFile(e.target.files?.[0]??null)}/><button onClick={upload} disabled={!userId || !accountId || !file || loading}>{loading?'Uploading...':'Upload'}</button></div>{loading&&<LoadingMessage message='Uploading transactions...'/>}<ErrorMessage message={error} />{result&&<div className='panel'><div className='grid'><StatCard label='totalRows' value={result.totalRows} /><StatCard label='createdCount' value={result.createdCount} /><StatCard label='skippedDuplicateCount' value={result.skippedDuplicateCount} /><StatCard label='failedCount' value={result.failedCount} /></div>{result.failedCount>0 && <table><thead><tr><th>row</th><th>message</th></tr></thead><tbody>{result.errors.map((e,i)=><tr key={i}><td>{e.rowNumber}</td><td>{e.message}</td></tr>)}</tbody></table>}</div>}</AppShell>}
