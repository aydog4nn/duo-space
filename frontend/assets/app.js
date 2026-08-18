const toast = document.getElementById('toast');
const movieList = document.getElementById('movieList');
const movieDialog = document.getElementById('movieDialog');
const state = { token: localStorage.getItem('duoSpaceToken'), room: JSON.parse(localStorage.getItem('duoSpaceRoom') || 'null'), authMode: 'login' };
const showToast = message => { toast.textContent = message; toast.classList.add('show'); setTimeout(() => toast.classList.remove('show'), 2400); };
const setVisible = (element, visible) => { element.hidden = !visible; };

async function request(path, options = {}) {
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  if (state.token) headers.Authorization = `Bearer ${state.token}`;
  const response = await fetch(`/api/v1${path}`, { ...options, headers });
  const data = response.status === 204 ? null : await response.json().catch(() => null);
  if (!response.ok) throw new Error(data?.message || 'İşlem tamamlanamadı.');
  return data;
}

function saveRoom(room) { state.room = room; localStorage.setItem('duoSpaceRoom', JSON.stringify(room)); }
function showAuth() {
  setVisible(document.getElementById('authForm'), true); setVisible(document.getElementById('roomForm'), false); setVisible(document.getElementById('roomConnected'), false);
  document.getElementById('connectionLabel').textContent = "DUOSPACE'E GİR"; document.getElementById('connectionTitle').textContent = 'Kendi köşeni aç'; document.getElementById('connectionDescription').textContent = 'Listenizi gerçekten birlikte tutmak için giriş yap.';
  renderWatchlist([]);
}
function showRoomSetup() {
  setVisible(document.getElementById('authForm'), false); setVisible(document.getElementById('roomForm'), true); setVisible(document.getElementById('roomConnected'), false);
  document.getElementById('connectionLabel').textContent = 'BİR ODA SEÇ'; document.getElementById('connectionTitle').textContent = 'İkinize ait alan hazırla'; document.getElementById('connectionDescription').textContent = 'Yeni bir oda oluştur veya sevdiğinin davet kodunu gir.';
}
function showConnectedRoom() {
  setVisible(document.getElementById('authForm'), false); setVisible(document.getElementById('roomForm'), false); setVisible(document.getElementById('roomConnected'), true);
  document.getElementById('connectionLabel').textContent = 'BİRLİKTE LİSTE'; document.getElementById('connectionTitle').textContent = 'Odanız bağlı'; document.getElementById('connectionDescription').textContent = 'Eklediğiniz her seçim ikinizin listesinde de durur.';
  document.getElementById('activeRoomName').textContent = state.room.name; document.getElementById('activeInviteCode').textContent = `Davet: ${state.room.inviteCode}`;
}
function renderWatchlist(items) {
  movieList.replaceChildren();
  const empty = text => movieList.append(Object.assign(document.createElement('li'), { className: 'empty-list', textContent: text }));
  if (!state.token) return empty('Listeyi görmek için giriş yap.');
  if (!state.room) return empty('Önce bir oda oluştur veya odaya katıl.');
  if (!items.length) return empty('Liste henüz boş. İlk seçimi sen ekle.');
  const colors = ['peach', 'purple', 'yellow']; const statuses = { PLANNED: 'Planlandı', WATCHING: 'İzleniyor', COMPLETED: 'İzlendi' };
  items.forEach((item, index) => {
    const li = document.createElement('li'); const poster = Object.assign(document.createElement('span'), { className: `mini-poster ${colors[index % colors.length]}` });
    const copy = document.createElement('div'); const title = document.createElement('b'); title.textContent = item.title; const detail = document.createElement('small'); detail.textContent = item.sourceUrl ? `Bağlantı eklendi · ${statuses[item.status]}` : statuses[item.status];
    copy.append(title, detail); li.append(poster, copy, Object.assign(document.createElement('span'), { className: 'drag', textContent: '⋮⋮' })); movieList.append(li);
  });
}
async function loadWatchlist() { if (!state.room) return renderWatchlist([]); try { renderWatchlist(await request(`/rooms/${state.room.id}/watchlist`)); } catch (error) { showToast(error.message); } }
async function initialize() {
  if (!state.token) return showAuth();
  try { const rooms = await request('/rooms'); const currentRoom = rooms.find(room => room.id === state.room?.id) || rooms[0]; if (!currentRoom) return showRoomSetup(); saveRoom(currentRoom); showConnectedRoom(); await loadWatchlist(); }
  catch (error) { localStorage.removeItem('duoSpaceToken'); localStorage.removeItem('duoSpaceRoom'); state.token = null; state.room = null; showAuth(); showToast('Oturumun bitti, tekrar giriş yapabilirsin.'); }
}
document.getElementById('authModeToggle').addEventListener('click', () => {
  state.authMode = state.authMode === 'login' ? 'register' : 'login'; const registering = state.authMode === 'register';
  setVisible(document.getElementById('displayNameField'), registering); document.getElementById('displayName').required = registering; document.getElementById('authSubmit').textContent = registering ? 'Kayıt ol' : 'Giriş yap'; document.getElementById('authModeToggle').textContent = registering ? 'Zaten hesabın var mı? Giriş yap' : 'Hesabın yok mu? Kayıt ol';
});
document.getElementById('authForm').addEventListener('submit', async event => {
  event.preventDefault(); const email = document.getElementById('email').value.trim(); const password = document.getElementById('password').value;
  try { if (state.authMode === 'register') await request('/auth/register', { method: 'POST', body: JSON.stringify({ username: document.getElementById('displayName').value.trim(), email, password }) }); const tokenResponse = await request('/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) }); state.token = tokenResponse.accessToken; localStorage.setItem('duoSpaceToken', state.token); showToast(state.authMode === 'register' ? 'Hesabın hazır.' : 'Tekrar hoş geldin.'); await initialize(); } catch (error) { showToast(error.message); }
});
document.getElementById('createRoomForm').addEventListener('submit', async event => { event.preventDefault(); try { saveRoom(await request('/rooms', { method: 'POST', body: JSON.stringify({ name: document.getElementById('roomName').value.trim() }) })); showConnectedRoom(); await loadWatchlist(); showToast('Odan hazır, davet kodunu paylaşabilirsin.'); } catch (error) { showToast(error.message); } });
document.getElementById('joinRoomForm').addEventListener('submit', async event => { event.preventDefault(); try { saveRoom(await request('/rooms/join', { method: 'POST', body: JSON.stringify({ inviteCode: document.getElementById('inviteCode').value.trim() }) })); showConnectedRoom(); await loadWatchlist(); showToast('Odaya katıldın.'); } catch (error) { showToast(error.message); } });
document.getElementById('logoutButton').addEventListener('click', () => { state.token = null; state.room = null; localStorage.removeItem('duoSpaceToken'); localStorage.removeItem('duoSpaceRoom'); showAuth(); showToast('Çıkış yapıldı.'); });
function renderMovieResults(movies) {
  const container = document.getElementById('movieSearchResults'); container.replaceChildren();
  if (!movies.length) { container.textContent = 'Sonuç bulunamadı.'; return; }
  movies.forEach(movie => {
    const button = document.createElement('button'); button.type = 'button'; button.className = 'movie-search-result';
    if (movie.posterUrl) { const poster = document.createElement('img'); poster.src = movie.posterUrl; poster.alt = ''; button.append(poster); }
    const copy = document.createElement('span'); const title = document.createElement('b'); title.textContent = movie.title; const detail = document.createElement('small'); detail.textContent = `${movie.releaseYear || 'Yıl bilgisi yok'} · TMDB puanı ${movie.voteAverage?.toFixed(1) || '-'}`; copy.append(title, detail); button.append(copy);
    button.addEventListener('click', () => { document.getElementById('movieTitle').value = movie.title; document.getElementById('movieSourceUrl').value = `https://www.themoviedb.org/movie/${movie.tmdbId}`; container.replaceChildren(); showToast('Film seçildi, şimdi listeye ekleyebilirsin.'); });
    container.append(button);
  });
}
document.getElementById('searchMovieButton').addEventListener('click', async () => {
  const query = document.getElementById('movieSearchQuery').value.trim();
  if (query.length < 2) return showToast('Film aramak için en az 2 karakter yaz.');
  try { renderMovieResults(await request(`/movies/search?query=${encodeURIComponent(query)}`)); } catch (error) { showToast(error.message); }
});
document.getElementById('addMovie').addEventListener('click', () => { if (!state.token || !state.room) return showToast('Önce giriş yapıp bir oda seçmelisin.'); movieDialog.showModal(); });
document.getElementById('movieForm').addEventListener('submit', async event => { event.preventDefault(); try { await request(`/rooms/${state.room.id}/watchlist`, { method: 'POST', body: JSON.stringify({ title: document.getElementById('movieTitle').value.trim(), sourceUrl: document.getElementById('movieSourceUrl').value.trim() || null }) }); movieDialog.close(); event.currentTarget.reset(); await loadWatchlist(); showToast('Listeye eklendi.'); } catch (error) { showToast(error.message); } });
let playing = false;
document.getElementById('playButton').addEventListener('click', event => { playing = !playing; event.currentTarget.textContent = playing ? 'Ⅱ' : '▶'; document.getElementById('watchStatus').textContent = playing ? 'Birlikte izliyorsunuz ✦' : 'Duraklatıldı'; document.getElementById('progressBar').style.width = '32%'; document.getElementById('timeText').textContent = '32:18 / 1:41:00'; showToast(playing ? 'Film ikiniz için de başlatıldı ✨' : 'Film duraklatıldı'); });
document.getElementById('likeButton').addEventListener('click', event => { event.currentTarget.textContent = event.currentTarget.textContent === '♡' ? '♥' : '♡'; showToast('Listeye küçük bir kalp bıraktın'); });
document.getElementById('planButton').addEventListener('click', () => showToast('Yeni date night fikri ekleme ekranı yakında burada!'));
document.getElementById('noteButton').addEventListener('click', () => showToast('Notlarınız yakında ikinizle senkron olacak.'));
document.getElementById('gameButton').addEventListener('click', () => showToast('İlk oyun: taş, kağıt, makas? 🎲'));
document.getElementById('messageForm').addEventListener('submit', event => { event.preventDefault(); const input = document.getElementById('messageInput'); const text = input.value.trim(); if (!text) return; const message = document.createElement('p'); message.className = 'message me'; message.append(text, Object.assign(document.createElement('small'), { textContent: 'şimdi' })); document.getElementById('messages').append(message); input.value = ''; });
initialize();
