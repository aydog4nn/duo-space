import test from 'node:test';
import assert from 'node:assert/strict';
import { getWebLink } from '../src/utils/webLink.js';

for (const value of [null, undefined, '', 123, {}, 'javascript:alert(1)', 'data:text/html,test',
  'file:///etc/passwd', 'ftp://example.com', '//example.com', '/movie/1', 'https://', 'https:///example.com',
  'https://user:password@example.com', 'https://example.com:99999', 'https://example.com:abc',
  ' https://example.com', 'https://example.com/a b', 'https://example.com/\nfilm', 'https:\\example.com']) {
  test(`Geçersiz bağlantı açılmaz: ${JSON.stringify(value)}`, () => {
    assert.equal(getWebLink(value), null);
  });
}

for (const [value, expected] of [
  ['https://example.com/movie/1', 'https://example.com/movie/1'],
  ['http://example.com', 'http://example.com/'],
  ['HTTPS://example.com/a?q=film#detail', 'https://example.com/a?q=film#detail'],
]) {
  test(`Web bağlantısı korunur: ${value}`, () => assert.equal(getWebLink(value), expected));
}
