const nextStatus = { PLANNED: 'WATCHING', WATCHING: 'COMPLETED', COMPLETED: 'PLANNED' };
const statusLabel = { PLANNED: 'Planlandı', WATCHING: 'İzleniyor', COMPLETED: 'İzlendi' };

export default function Watchlist({ items, onStatusChange, onDelete, busy }) {
  if (!items.length) return <div className="empty-state"><span>✦</span><strong>Liste henüz boş.</strong><p>İlk filmi ya da oyunu ekleyerek başlayın.</p></div>;

  return <ul className="watchlist">
    {items.map((item, index) => <li key={item.id} className="watchlist-item">
      <span className={`poster-placeholder poster-${index % 3}`}>{item.title.slice(0, 1).toUpperCase()}</span>
      <div className="item-copy"><strong>{item.title}</strong><small>{item.sourceUrl ? 'Bağlantı eklendi' : 'Bağlantı yok'}</small></div>
      <button className={`status-pill ${item.status.toLowerCase()}`} disabled={busy} onClick={() => onStatusChange(item, nextStatus[item.status])}>{statusLabel[item.status]}</button>
      <button className="delete-button" disabled={busy} onClick={() => onDelete(item.id)} aria-label={`${item.title} kaydını sil`}>×</button>
    </li>)}
  </ul>;
}
