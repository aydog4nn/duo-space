export function getWebLink(value) {
  if (typeof value !== 'string' || !/^https?:\/\/[^/?#]/i.test(value)) return null;
  // Tarayıcının boşluk ve ters eğik çizgileri düzelterek adresi değiştirmesine izin verme.
  if (/[\s\\\u0000-\u001f\u007f]/u.test(value)) return null;
  try {
    const url = new URL(value);
    if (!url.hostname || url.username || url.password) return null;
    return url.href;
  } catch {
    return null;
  }
}
