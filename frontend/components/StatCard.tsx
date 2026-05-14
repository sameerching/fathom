export default function StatCard({ label, value, tone='neutral', icon='💠' }: { label: string; value: string | number; tone?: 'positive'|'negative'|'neutral'; icon?: string }) {
  const toneColor = tone === 'positive' ? 'var(--success)' : tone === 'negative' ? 'var(--danger)' : 'var(--text-primary)';
  return <article className='card' style={{background:'linear-gradient(145deg, rgba(124,155,255,.13), rgba(255,255,255,.03))'}}><div className='row' style={{justifyContent:'space-between'}}><span className='muted'>{icon} {label}</span></div><div className='metric-value' style={{color:toneColor}}>{value}</div></article>;
}
