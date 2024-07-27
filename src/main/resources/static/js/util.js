/**
 * Questo script evidenzia dinamicamente il link del menu corrispondente alla pagina corrente.
 *
 * - Quando il documento è completamente caricato, ottiene l'URL della pagina attuale.
 * - Seleziona tutti i link nel menu di navigazione.
 * - Confronta l'URL della pagina corrente con l'URL di ciascun link nel menu.
 * - Aggiunge la classe 'active' al link corrispondente se l'URL coincide, evidenziandolo.
 */
document.addEventListener("DOMContentLoaded", function() {
    var currentUrl = window.location.href;
    var menuLinks = document.querySelectorAll(".navbar-nav a.nav-link");
    menuLinks.forEach(function(link) {
        var linkUrl = link.href;
        if (currentUrl === linkUrl) {
            link.classList.add("active");
        } else {
            link.classList.remove("active");
        }
    });
});

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
