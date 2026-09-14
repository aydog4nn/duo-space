import { getWebLink } from '../utils/webLink';

export default function SourceLink({ value }) {
  if (value == null || value === '') return null;
  const href = getWebLink(value);
  if (!href) return <p>Bu bağlantı geçersiz olduğu için açılamıyor.</p>;
  return <a href={href} target="_blank" rel="noopener noreferrer">Kaynağı aç ↗</a>;
}
