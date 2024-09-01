
/**
 * Questo script evidenzia dinamicamente il link del menu corrispondente alla pagina corrente.
 *
 * - Quando il documento è completamente caricato, ottiene l'URL della pagina attuale.
 * - Seleziona tutti i link nel menu di navigazione.
 * - Confronta l'URL della pagina corrente con l'URL di ciascun link nel menu.
 * - Aggiunge la classe 'active' al link corrispondente se l'URL coincide, evidenziandolo.
 */
// Funzione per evidenziare il link attivo
function highlightActiveMenuLink() {
    // Ottieni l'URL della pagina corrente
    var currentUrl = window.location.pathname;
    // Seleziona tutti i link nel menu di navigazione
    var menuLinks = document.querySelectorAll(".navbar-nav .nav-link");
    // Itera su ciascun link e confronta l'URL
    menuLinks.forEach(function(link) {
        // Ottieni l'URL del link (rimuove query params e hash)
        var linkUrl = new URL(link.href).pathname;
        // Aggiungi la classe 'active' se l'URL corrisponde
        if (currentUrl === linkUrl) {
            link.classList.add("active");
        } else {
            link.classList.remove("active");
        }
    });
}



/**
 * funzione che serve a visualizzare la finestra di attesa quando si fa submit
 */
function showLoadingOverlay() {
    document.getElementById('loadingOverlay').style.display = 'flex';
}


<!-- per fare la WPA -->
if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
        navigator.serviceWorker.register('/service-worker.js')
            .then(registration => {
                console.log('Service Worker registered with scope:', registration.scope);
            })
            .catch(error => {
                console.error('Service Worker registration failed:', error);
            });
    });
}
