importScripts('https://www.gstatic.com/firebasejs/10.12.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/10.12.0/firebase-messaging-compat.js');

firebase.initializeApp({
  apiKey: "AIzaSyDE1E4vN-3FFPn0OCyUuvE1fZizjsofYwk",
  authDomain: "didlin.firebaseapp.com",
  projectId: "didlin",
  storageBucket: "didlin.firebasestorage.app",
  messagingSenderId: "599992082188",
  appId: "1:599992082188:web:9924580056179ab634f329",
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage((payload) => {
  console.log('[firebase-messaging-sw.js] Pesan masuk di background:', payload);
  
  const title = payload.notification?.title || payload.data?.title || 'Notifikasi Ruangkelas';
  const options = {
    body: payload.notification?.body || payload.data?.body || '',
    icon: '/icons/icon-192.png',
    data: payload
  };

  self.registration.showNotification(title, options);
});

self.addEventListener('notificationclick', (event) => {
  event.notification.close();
  
  let targetUrl = '/';
  const payload = event.notification.data || {};

  console.log('[notificationclick] full payload:', JSON.stringify(payload));

  const dataBlock = payload.data 
    || payload.FCM_MSG?.data 
    || payload.FCM_MSG?.notification?.data
    || {};

  console.log('[notificationclick] dataBlock:', JSON.stringify(dataBlock));

  if (payload.fcmOptions?.link) {
      targetUrl = payload.fcmOptions.link;
  } else if (payload.FCM_MSG?.fcmOptions?.link) {
      targetUrl = payload.FCM_MSG.fcmOptions.link;
  } else if (dataBlock.referenceType && dataBlock.referenceId) {
      targetUrl = `/notifications/redirect?type=${dataBlock.referenceType}&id=${dataBlock.referenceId}`;
  }

  console.log('[notificationclick] targetUrl:', targetUrl);

  const finalUrl = new URL(targetUrl, self.location.origin).href;

  event.waitUntil(
      clients.matchAll({ type: 'window', includeUncontrolled: true }).then((clientList) => {
          for (let i = 0; i < clientList.length; i++) {
              let client = clientList[i];
              if (client.url.startsWith(self.location.origin) && 'focus' in client) {
                  client.focus();
                  if ('navigate' in client) {
                      return client.navigate(finalUrl);
                  }
              }
          }
          if (clients.openWindow) {
              return clients.openWindow(finalUrl);
          }
      })
  );
});