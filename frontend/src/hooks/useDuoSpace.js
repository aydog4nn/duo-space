import { useCallback, useEffect, useState } from 'react';
import { apiRequest, ApiError } from '../api/client';
import { clearSession, clearStoredRoom, loadSession, persistSession } from '../utils/storage';

export function useDuoSpace() {
  const storedSession = loadSession();
  const [token, setToken] = useState(storedSession.token);
  const [room, setRoom] = useState(storedSession.room);
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(Boolean(storedSession.token));

  const logout = useCallback(() => {
    clearSession();
    setToken(null);
    setRoom(null);
    setItems([]);
  }, []);

  const request = useCallback(async (path, options) => {
    try {
      return await apiRequest(path, token, options);
    } catch (error) {
      if (error instanceof ApiError && error.status === 401) logout();
      throw error;
    }
  }, [logout, token]);

  const refreshWatchlist = useCallback(async activeRoom => {
    if (!activeRoom) return setItems([]);
    const data = await request(`/rooms/${activeRoom.id}/watchlist`);
    setItems(data);
  }, [request]);

  const initialize = useCallback(async () => {
    if (!token) return setLoading(false);
    setLoading(true);
    try {
      const rooms = await request('/rooms');
      const activeRoom = rooms.find(candidate => candidate.id === room?.id) || rooms[0] || null;
      setRoom(activeRoom);
      if (activeRoom) {
        persistSession(token, activeRoom);
        await refreshWatchlist(activeRoom);
      } else {
        clearStoredRoom();
        setItems([]);
      }
    } finally {
      setLoading(false);
    }
  }, [refreshWatchlist, request, room?.id, token]);

  useEffect(() => { initialize().catch(() => {}); }, [initialize]);

  async function authenticate({ mode, username, email, password }) {
    if (mode === 'register') await apiRequest('/auth/register', null, { method: 'POST', body: JSON.stringify({ username, email, password }) });
    const response = await apiRequest('/auth/login', null, { method: 'POST', body: JSON.stringify({ email, password }) });
    clearStoredRoom();
    setToken(response.accessToken);
    setRoom(null);
    setItems([]);
    persistSession(response.accessToken, null);
  }

  async function createRoom(name) {
    const createdRoom = await request('/rooms', { method: 'POST', body: JSON.stringify({ name }) });
    setRoom(createdRoom);
    persistSession(token, createdRoom);
    setItems([]);
  }

  async function joinRoom(inviteCode) {
    const joinedRoom = await request('/rooms/join', { method: 'POST', body: JSON.stringify({ inviteCode }) });
    setRoom(joinedRoom);
    persistSession(token, joinedRoom);
    await refreshWatchlist(joinedRoom);
  }

  async function addItem({ title, sourceUrl }) {
    await request(`/rooms/${room.id}/watchlist`, { method: 'POST', body: JSON.stringify({ title, sourceUrl: sourceUrl || null }) });
    await refreshWatchlist(room);
  }

  async function updateItem(item, status) {
    await request(`/rooms/${room.id}/watchlist/${item.id}`, { method: 'PUT', body: JSON.stringify({ title: item.title, sourceUrl: item.sourceUrl, status }) });
    await refreshWatchlist(room);
  }

  async function removeItem(itemId) {
    await request(`/rooms/${room.id}/watchlist/${itemId}`, { method: 'DELETE' });
    await refreshWatchlist(room);
  }

  function searchMovies(query) {
    return request(`/movies/search?query=${encodeURIComponent(query)}`);
  }

  return { token, room, items, loading, authenticate, createRoom, joinRoom, addItem, updateItem, removeItem, searchMovies, logout };
}
