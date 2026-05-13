export default function LoadingMessage({ message = 'Loading...' }: { message?: string }) {
  return <p className="muted">{message}</p>;
}
