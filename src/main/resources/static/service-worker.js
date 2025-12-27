// Nome della cache dove salveremo le risorse
const CACHE_NAME = 'pwa-cache-v1';

// Lista di URL da memorizzare nella cache quando il service worker viene installato
const urlsToCache = [
    '/', // la root della pagina
    '/css/style.css', // CSS principale
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
    // event.waitUntil fa sì che il service worker non venga considerato installato finché la promise non si risolve
    event.waitUntil(
        caches.open(CACHE_NAME) // apre o crea la cache con il nome CACHE_NAME
            .then(cache => cache.addAll(urlsToCache)) // aggiunge tutti gli URL alla cache
    );
});

// Evento 'fetch': intercetta tutte le richieste di rete
self.addEventListener('fetch', event => {
    // Se la richiesta è per la root ('/'), fetch sempre dal server
    if (event.request.url.endsWith('/')) {
        event.respondWith(fetch(event.request)); // bypassa la cache per la home
    } else {
        // Per tutte le altre richieste, prima prova a servire dalla cache
        event.respondWith(
            caches.match(event.request) // cerca la risorsa nella cache
                .then(response => response || fetch(event.request))
            // se la trovi in cache, la restituisce; altrimenti la prende dalla rete
        );
    }
});

// Evento 'activate': viene eseguito quando il service worker diventa attivo
// Serve a pulire vecchie cache che non usiamo più
self.addEventListener('activate', event => {
    const cacheWhitelist = [CACHE_NAME]; // cache da mantenere
    event.waitUntil(
        caches.keys().then(cacheNames =>
            Promise.all(
                cacheNames.map(cacheName => {
                    // Se la cache non è nella whitelist, la elimino
                    if (cacheWhitelist.indexOf(cacheName) === -1) {
                        return caches.delete(cacheName);
                    }
                })
            )
        )
    );
});
