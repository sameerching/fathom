const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? 'http://localhost:8080';
async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const headers: Record<string, string> = { ...(init?.headers as Record<string, string> | undefined) };
  if (!(init?.body instanceof FormData)) headers['Content-Type'] = headers['Content-Type'] ?? 'application/json';
  const response = await fetch(`${API_BASE_URL}${path}`, { ...init, headers });
  if (!response.ok) throw new Error((await response.text()) || `Request failed ${response.status}`);
  return response.json() as Promise<T>;
}
export type MonthlySummary = { income:number;expenses:number;investments:number;liabilityPayments:number;netCashFlow:number;savingsRate:number };
export type CategoryBreakdownItem = { categoryName:string;amount:number;transactionCount:number };
export type NetWorthSummary = { totalAssets:number;totalLiabilities:number;netWorth:number };
export type Transaction = { id:string;transactionDate:string;direction:string;amount:number;transactionType:string;merchant:string|null;rawDescription:string|null;accountId:string;categoryId:string|null };
export type ImportSummary = { status:string;totalRows:number;createdCount:number;skippedDuplicateCount:number;failedCount:number;errors:{rowNumber:number;message:string}[] };
export type Account = { id:string;name:string;accountType:string };
export type InvestmentHolding = { id:string;assetType:string;name:string;provider:string|null;symbol:string|null;currency:string|null;investedAmount:number|null;currentValue:number|null;asOfDate:string|null };
export type Liability = { id:string;liabilityType:string;name:string;lender:string|null;currency:string|null;principalAmount:number|null;outstandingAmount:number;interestRate:number|null;emiAmount:number|null;startDate:string|null;endDate:string|null };
export const getMonthlySummary = (userId:string, month:string)=>request<MonthlySummary>(`/api/users/${userId}/dashboard/monthly-summary?month=${month}`);
export const getCategoryBreakdown = (userId:string,from:string,to:string,type:string)=>request<CategoryBreakdownItem[]>(`/api/users/${userId}/dashboard/category-breakdown?${new URLSearchParams({from,to,type})}`);
export const getNetWorth = (userId:string)=>request<NetWorthSummary>(`/api/users/${userId}/dashboard/net-worth`);
export const getTransactions = (userId:string, filters:Record<string,string>)=>{ const p=new URLSearchParams(); Object.entries(filters).forEach(([k,v])=>v&&p.set(k,v)); return request<Transaction[]>(`/api/users/${userId}/transactions?${p}`); };
export const uploadTransactionsCsv = async (userId:string,accountId:string,file:File,source:string)=>{ const fd=new FormData(); fd.append('file',file); return request<ImportSummary>(`/api/users/${userId}/accounts/${accountId}/transaction-imports?source=${source}`,{method:'POST',body:fd}); };
export const getUserAccounts = (userId:string)=>request<Account[]>(`/api/users/${userId}/accounts`);
export const getInvestmentHoldings = (userId:string)=>request<InvestmentHolding[]>(`/api/users/${userId}/investment-holdings`);
export const createInvestmentHolding = (userId:string,payload:Record<string,unknown>)=>request<InvestmentHolding>(`/api/users/${userId}/investment-holdings`,{method:'POST',body:JSON.stringify(payload)});
export const getLiabilities = (userId:string)=>request<Liability[]>(`/api/users/${userId}/liabilities`);
export const createLiability = (userId:string,payload:Record<string,unknown>)=>request<Liability>(`/api/users/${userId}/liabilities`,{method:'POST',body:JSON.stringify(payload)});
