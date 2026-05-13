export default function ErrorMessage({ message }: { message: string }) {
  if (!message) return null;
  return <p className="warn">{message}</p>;
}
