export function formatCurrency(value: number | string | null | undefined, currency = 'INR') {
  const numericValue = Number(value ?? 0);
  if (Number.isNaN(numericValue)) return '-';
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency,
    maximumFractionDigits: 2
  }).format(numericValue);
}

export function formatPercent(value: number | string | null | undefined) {
  const numericValue = Number(value ?? 0);
  if (Number.isNaN(numericValue)) return '-';
  return `${numericValue.toFixed(1)}%`;
}

export function formatDate(value: string | number | Date) {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return String(value);
  return new Intl.DateTimeFormat('en-IN', { year: 'numeric', month: 'short', day: '2-digit' }).format(date);
}

export function compactId(id: string) {
  if (!id) return '';
  if (id.length <= 12) return id;
  return `${id.slice(0, 6)}...${id.slice(-4)}`;
}
