import { useState } from 'react';

export default function RoomSetup({ onCreate, onJoin, busy }) {
  const [roomName, setRoomName] = useState('');
  const [inviteCode, setInviteCode] = useState('');

  return <section className="access-card">
    <div className="access-copy"><p className="eyebrow">BİR ODA SEÇ</p><h2>İkinize ait alan.</h2><p>Yeni bir oda oluştur veya sana gelen davet koduyla mevcut odaya katıl.</p></div>
    <div className="room-forms">
      <form onSubmit={event => { event.preventDefault(); onCreate(roomName.trim()); }}><label>Yeni oda<input value={roomName} onChange={event => setRoomName(event.target.value)} maxLength="100" required placeholder="Odamızın adı" /></label><button className="primary-button" disabled={busy}>Oda oluştur</button></form>
      <p className="or-divider">veya</p>
      <form onSubmit={event => { event.preventDefault(); onJoin(inviteCode.trim()); }}><label>Davet kodu<input value={inviteCode} onChange={event => setInviteCode(event.target.value)} maxLength="16" required placeholder="Örn. D9K4X2" /></label><button className="secondary-button" disabled={busy}>Odaya katıl</button></form>
    </div>
  </section>;
}
