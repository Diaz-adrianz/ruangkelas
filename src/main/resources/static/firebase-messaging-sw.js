// ================================================
// Firebase Messaging Service Worker
// Taruh di: src/main/resources/static/firebase-messaging-sw.js
// WAJIB: Tambahkan file ini ke .gitignore!
// ================================================

importScripts('https://www.gstatic.com/firebasejs/10.12.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/10.12.0/firebase-messaging-compat.js');

// Config harus hardcode di sini (service worker tidak bisa baca Thymeleaf)
firebase.initializeApp({
    apiKey:            "AIzaSyDE1E4vN-3FFPn0OCyUuvE1fZizjsofYwk",
    authDomain:        "didlin.firebaseapp.com",
    projectId:         "didlin",
    storageBucket:     "didlin.appspot.com",
    messagingSenderId: "599992082188",
    appId:             "1:599992082188:web:9924580056179ab634f329"
});

const messaging = firebase.messaging();

// Handle notifikasi saat app di background / tab tertutup
messaging.onBackgroundMessage((payload) => {
    console.log('[SW] Background message:', payload);

    const title   = payload.notification?.title || 'Notifikasi Baru';
    const options = {
        body:  payload.notification?.body || '',
        icon:  '/images/logo.png',   // sesuaikan path icon kamu
        badge: '/images/badge.png',  // opsional
        data:  payload.data
    };

    return self.registration.showNotification(title, options);
});