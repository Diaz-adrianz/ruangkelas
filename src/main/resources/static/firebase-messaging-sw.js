// firebase-messaging-sw.js
// Service worker buat handle FCM push notification pas tab/browser di background.

importScripts('https://www.gstatic.com/firebasejs/10.13.2/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/10.13.2/firebase-messaging-compat.js');

// !! GANTI dengan config Firebase yang SAMA dengan yang dipakai di JS utama (tempat getToken() dipanggil) !!
firebase.initializeApp({
  apiKey: "AIzaSyDE1E4vN-3FFPn0OCyUuvE1fZizjsofYwk",
  authDomain: "didlin.firebaseapp.com",
  projectId: "didlin",
  storageBucket: "didlin.firebasestorage.app",
  messagingSenderId: "599992082188",
  appId: "1:599992082188:web:9924580056179ab634f329",
});

const messaging = firebase.messaging();

// Dipanggil otomatis kalau ada push masuk SAAT tab/app lagi di background atau tertutup
messaging.onBackgroundMessage((payload) => {
  console.log('[firebase-messaging-sw.js] Pesan diterima di background:', payload);

  const title = payload.notification?.title || payload.data?.title || 'Notifikasi Ruangkelas';
  const options = {
    body: payload.notification?.body || payload.data?.body || '',
    icon: '/icons/icon-192.png', // sesuaikan path icon kalau ada, atau hapus baris ini
    data: payload.data || {} // Data payload (termasuk referenceId & referenceType) otomatis masuk ke sini
  };

  self.registration.showNotification(title, options);
});

// HANDLER KETIKA NOTIFIKASI DIKLIK
self.addEventListener('notificationclick', (event) => {
  event.notification.close(); // Tutup pop-up notifikasi terlebih dahulu

  // Ambil data payload
  const data = event.notification.data || {};
  const referenceId = data.referenceId;
  const referenceType = data.referenceType;

  // Default redirect ke halaman utama jika tidak ada tipe referensi
  let redirectPath = '/';

  if (referenceType && referenceId) {
      redirectPath = `/notifications/redirect?type=${referenceType}&id=${referenceId}`;
  }

  // client.url dari clients.matchAll() selalu absolute URL, jadi redirectPath
  // (yang relative) harus dijadiin absolute dulu sebelum dibandingin —
  // kalau enggak, perbandingan client.url === redirectPath gak akan pernah match.
  const targetUrl = new URL(redirectPath, self.location.origin).href;

  // Arahkan tab browser yang sudah ada, atau buka tab baru jika belum ada yang aktif
  event.waitUntil(
      clients.matchAll({ type: 'window', includeUncontrolled: true }).then(function(clientList) {
          for (let i = 0; i < clientList.length; i++) {
              let client = clientList[i];
              if (client.url === targetUrl && 'focus' in client) {
                  return client.focus();
              }
          }
          if (clients.openWindow) {
              return clients.openWindow(targetUrl);
          }
      })
  );
});