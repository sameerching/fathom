export default function LoadingMessage({ message='Loading...' }: { message?: string }) { return <div className='panel muted'>⏳ {message}</div>; }
