import { useState } from 'react';
import { useDuoSpace } from './hooks/useDuoSpace';
import AuthCard from './components/AuthCard';
import RoomSetup from './components/RoomSetup';
import Watchlist from './components/Watchlist';
import AddItemDialog from './components/AddItemDialog';

export default function App() {
  const duoSpace = useDuoSpace();
  const [notice, setNotice] = useState('');
  const [busy, setBusy] = useState(false);
  const [isDialogOpen, setDialogOpen] = useState(false);
  const featured = duoSpace.items.find(item => item.status === 'WATCHING') || duoSpace.items[0];

  async function run(action, successMessage) {
    setBusy(true);
    try { await action(); setNotice(successMessage); } catch (error) { setNotice(error.message); } finally { setBusy(false); }
  }

  function handleAuth(values) { return run(() => duoSpace.authenticate(values), values.mode === 'register' ? 'Hesabın hazır, şimdi bir oda seç.' : 'Tekrar hoş geldin.'); }
  function handleCreate(name) { return run(() => duoSpace.createRoom(name), 'Oda oluşturuldu. Davet kodunu paylaşabilirsin.'); }
  function handleJoin(inviteCode) { return run(() => duoSpace.joinRoom(inviteCode), 'Odaya katıldın.'); }
  function handleAdd(item) { return run(async () => { await duoSpace.addItem(item); setDialogOpen(false); }, 'Listeye eklendi.'); }
  function handleLogout() { duoSpace.logout(); setNotice('Çıkış yapıldı.'); }

  return <main className="app-shell">
    <header className="topbar"><a className="brand" href="/"><span>♥</span>Duo<span>Space</span></a><div className="session-state"><i className={duoSpace.room ? 'active' : ''} /><span>{duoSpace.room ? `${duoSpace.room.name} odası bağlı` : 'Oda bekleniyor'}</span>{duoSpace.token && <button onClick={handleLogout}>Çıkış</button>}</div></header>
    <section className="hero"><p className="eyebrow">ORTAK LİSTE · DUOSPACE</p><h1>Bu akşam <em>bizim.</em></h1><p>{duoSpace.room ? `Davet kodunuz: ${duoSpace.room.inviteCode}` : 'Birlikte seçin, birlikte planlayın.'}</p></section>

    {duoSpace.loading ? <section className="loading-card">Odan ve listen hazırlanıyor...</section> : !duoSpace.token ? <AuthCard onAuthenticate={handleAuth} busy={busy} /> : !duoSpace.room ? <RoomSetup onCreate={handleCreate} onJoin={handleJoin} busy={busy} /> : <section className="workspace">
      <article className="featured-card"><div className="featured-glow" /><div className="featured-copy"><p className="eyebrow">{featured ? featured.status === 'WATCHING' ? 'ŞİMDİ İZLENİYOR' : 'SIRADAKİ SEÇİM' : 'ORTAK LİSTEDEN'}</p><h2>{featured?.title || 'Bir seçim bekliyor'}</h2><p>{featured ? 'Listeden durumu değiştirerek akşamın planını güncelle.' : 'İlk filmi ya da oyunu ekleyerek başlayın.'}</p>{featured?.sourceUrl && <a href={featured.sourceUrl} target="_blank" rel="noreferrer">Kaynağı aç ↗</a>}</div><div className="featured-badge">{duoSpace.items.length}<small>seçim</small></div></article>
      <article className="room-card"><p className="eyebrow">AKTİF ODA</p><h2>{duoSpace.room.name}</h2><p>Davet kodu</p><code>{duoSpace.room.inviteCode}</code><button className="text-button" onClick={() => { navigator.clipboard?.writeText(duoSpace.room.inviteCode); setNotice('Davet kodu kopyalandı.'); }}>Kodu kopyala</button></article>
      <article className="list-card"><header><div><p className="eyebrow">BİRLİKTE LİSTE</p><h2>Sıradaki keyifler</h2></div><button className="add-button" onClick={() => setDialogOpen(true)} aria-label="Listeye ekle">+</button></header><Watchlist items={duoSpace.items} busy={busy} onStatusChange={(item, status) => run(() => duoSpace.updateItem(item, status), 'Liste durumu güncellendi.')} onDelete={itemId => run(() => duoSpace.removeItem(itemId), 'Listeden kaldırıldı.')} /></article>
    </section>}

    {notice && <button className="toast" onClick={() => setNotice('')}>{notice}</button>}
    {isDialogOpen && <AddItemDialog busy={busy} onClose={() => setDialogOpen(false)} onAdd={handleAdd} onSearch={duoSpace.searchMovies} />}
  </main>;
}
