// Nome della cache dove salveremo le risorse
const CACHE_NAME = 'pwa-cache-v3';

// Lista di URL da memorizzare nella cache quando il service worker viene installato
const urlsToCache = [
    '/', // la root della pagina
    '/css/style.css', // CSS principale
    '/js/util.js',
    '/js/1.5.2.sockjs.min.js', // libreria JS SockJS
    '/js/2.3.3.stomp.min.js', // libreria JS STOMP
    '/js/jquery-3.6.0.min.js', // jQuery
    '/js/1.13.1.jquery-ui.min.js', // jQuery UI
    '/js/bootstrap.5.2.3.bundle.min.js', // Bootstrap JS
    '/moon_256.png', // icona piccola
    '/moon_512.png'  // icona grande
    // Puoi aggiungere altre risorse statiche da mettere in cache
];

// Evento 'install': viene eseguito quando il service worker viene installato
// Serve a precaricare le risorse indicate nella cache
self.addEventListener('install', event => {
    // Attiva subito il nuovo service worker
    self.skipWaiting();
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then(cache => cache.addAll(urlsToCache))
    );
});

self.addEventListener('fetch', event => {
    // Il service worker gestisce solo richieste GET.
    // Le richieste POST/PUT/DELETE vengono lasciate direttamente al browser
    // perché possono contenere dati dinamici, CSRF token o chiamate API.
    if(event.request.method !== 'GET'){
        return;
    }
    // Per la home non usare la cache.
    // Viene sempre richiesta al server per avere sempre la versione aggiornata.
    if(event.request.url.endsWith('/')) {
        event.respondWith(
            fetch(event.request)
                .catch(() => caches.match('/'))
        );
    } else {
        event.respondWith(
            caches.match(event.request)
                .then(response => {
                    // Se la risorsa è in cache la restituisce,
                    // altrimenti prova a scaricarla dal server.
                    return response || fetch(event.request);

                })
        );
    }
});

// Evento 'activate': viene eseguito quando il service worker diventa attivo
// Serve a pulire vecchie cache che non usiamo più
self.addEventListener('activate', event => {
    event.waitUntil(
        Promise.all([
            // Il nuovo service worker prende subito il controllo
            clients.claim(),
            // Cancella cache vecchie
            caches.keys().then(cacheNames =>
                Promise.all(
                    cacheNames.map(cacheName => {
                        if(cacheName !== CACHE_NAME){
                            return caches.delete(cacheName);
                        }
                    })
                )
            )
        ])
    );
});
