import { useState } from 'react';

export default function AuthCard({ onAuthenticate, busy }) {
  const [mode, setMode] = useState('login');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const isRegister = mode === 'register';

  function submit(event) {
    event.preventDefault();
    onAuthenticate({ mode, username: username.trim(), email: email.trim(), password });
  }

  return <section className="access-card">
    <div className="access-copy"><p className="eyebrow">DUOSPACE'E GİR</p><h2>Kendi köşeni aç.</h2><p>Odanı oluştur, davet kodunu paylaş ve ortak listenizi aynı yerde tut.</p></div>
    <form className="access-form" onSubmit={submit}>
      {isRegister && <label>Adın<input value={username} onChange={event => setUsername(event.target.value)} minLength="3" maxLength="50" required placeholder="Örn. Akın" /></label>}
      <label>E-posta<input value={email} onChange={event => setEmail(event.target.value)} type="email" required placeholder="ornek@mail.com" /></label>
      <label>Şifre<input value={password} onChange={event => setPassword(event.target.value)} type="password" minLength="8" maxLength="72" required placeholder="En az 8 karakter" /></label>
      <button className="primary-button" disabled={busy}>{busy ? 'Bekleniyor...' : isRegister ? 'Kayıt ol' : 'Giriş yap'}</button>
      <button className="text-button" type="button" onClick={() => setMode(isRegister ? 'login' : 'register')}>{isRegister ? 'Zaten hesabın var mı? Giriş yap' : 'Hesabın yok mu? Kayıt ol'}</button>
    </form>
  </section>;
}
