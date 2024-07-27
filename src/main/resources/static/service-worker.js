const CACHE_NAME = 'pwa-cache-v1';
const urlsToCache = [
    '/',
    '/css/style.css',
    '/js/1.5.2.sockjs.min.js',
    '/js/2.3.3.stomp.min.js',
    '/js/jquery-3.6.0.min.js',
    '/js/1.13.1.jquery-ui.min.js',
    '/js/bootstrap.5.2.3.bundle.min.js',
    '/moon_256.png',
    '/moon_512.png'
    // Aggiungi altri URL che desideri memorizzare nella cache
];

self.addEventListener('install', event => {
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then(cache => cache.addAll(urlsToCache))
    );
});

self.addEventListener('fetch', event => {
    event.respondWith(
        caches.match(event.request)
            .then(response => response || fetch(event.request))
    );
});

self.addEventListener('activate', event => {
    const cacheWhitelist = [CACHE_NAME];
    event.waitUntil(
        caches.keys().then(cacheNames =>
            Promise.all(
                cacheNames.map(cacheName => {
                    if (cacheWhitelist.indexOf(cacheName) === -1) {
                        return caches.delete(cacheName);
                    }
                })
            )
        )
    );
});
