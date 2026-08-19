const TOKEN_KEY = 'duoSpaceToken';
const ROOM_KEY = 'duoSpaceRoom';
const storage = sessionStorage;

export function loadSession() {
  try {
    return { token: storage.getItem(TOKEN_KEY), room: JSON.parse(storage.getItem(ROOM_KEY) || 'null') };
  } catch {
    return { token: null, room: null };
  }
}

export function persistSession(token, room) {
  if (token) storage.setItem(TOKEN_KEY, token);
  if (room) storage.setItem(ROOM_KEY, JSON.stringify(room));
}

export function clearStoredRoom() {
  storage.removeItem(ROOM_KEY);
}

export function clearSession() {
  storage.removeItem(TOKEN_KEY);
  storage.removeItem(ROOM_KEY);
}
