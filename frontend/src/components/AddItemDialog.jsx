import { useState } from 'react';

export default function AddItemDialog({ onClose, onAdd, onSearch, busy }) {
  const [title, setTitle] = useState('');
  const [sourceUrl, setSourceUrl] = useState('');
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [searching, setSearching] = useState(false);
  const [searchError, setSearchError] = useState('');

  async function search() {
    if (query.trim().length < 2) return setSearchError('Arama için en az 2 karakter gir.');
    setSearching(true); setSearchError('');
    try { setResults(await onSearch(query.trim())); } catch (error) { setSearchError(error.message); } finally { setSearching(false); }
  }

  function selectMovie(movie) {
    setTitle(movie.title);
    setSourceUrl(`https://www.themoviedb.org/movie/${movie.tmdbId}`);
    setResults([]);
  }

  function submit(event) {
    event.preventDefault();
    onAdd({ title: title.trim(), sourceUrl: sourceUrl.trim() });
  }

  return <div className="dialog-backdrop" role="presentation" onMouseDown={onClose}>
    <section className="dialog" role="dialog" aria-modal="true" aria-labelledby="add-item-title" onMouseDown={event => event.stopPropagation()}>
      <header><div><p className="eyebrow">YENİ SEÇİM</p><h2 id="add-item-title">Listeye ekle</h2></div><button className="close-button" onClick={onClose} aria-label="Kapat">×</button></header>
      <div className="search-box"><label>Film ara<input value={query} onChange={event => setQuery(event.target.value)} maxLength="100" placeholder="Örn. About Time" /></label><button className="secondary-button" type="button" onClick={search} disabled={searching}>{searching ? 'Aranıyor...' : 'TMDB’de ara'}</button></div>
      {searchError && <p className="inline-error">{searchError}</p>}
      {results.length > 0 && <div className="search-results">{results.map(movie => <button type="button" key={movie.tmdbId} onClick={() => selectMovie(movie)}><span>{movie.posterUrl ? <img src={movie.posterUrl} alt="" /> : '✦'}</span><div><strong>{movie.title}</strong><small>{movie.releaseYear || 'Yıl bilgisi yok'} · Puan {movie.voteAverage?.toFixed(1) || '-'}</small></div></button>)}</div>}
      <form onSubmit={submit}><label>Başlık<input value={title} onChange={event => setTitle(event.target.value)} maxLength="255" required placeholder="Örn. Arrival" /></label><label>Bağlantı <em>isteğe bağlı</em><input value={sourceUrl} onChange={event => setSourceUrl(event.target.value)} type="url" maxLength="2048" placeholder="https://..." /></label><button className="primary-button" disabled={busy}>{busy ? 'Ekleniyor...' : 'Listeye ekle'}</button></form>
    </section>
  </div>;
}
