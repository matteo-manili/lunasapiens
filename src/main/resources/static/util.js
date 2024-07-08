/**
 * Questo script evidenzia dinamicamente il link del menu corrispondente alla pagina corrente.
 *
 * - Quando il documento è completamente caricato, ottiene l'URL della pagina attuale.
 * - Seleziona tutti i link nel menu di navigazione.
 * - Confronta l'URL della pagina corrente con l'URL di ciascun link nel menu.
 * - Aggiunge la classe 'active' al link corrispondente se l'URL coincide, evidenziandolo.
 */
document.addEventListener("DOMContentLoaded", function() {
    // Ottieni l'URL della pagina corrente
    var currentUrl = window.location.href;
    // Seleziona tutti i link nel menu
    var menuLinks = document.querySelectorAll(".navbar-nav a.nav-link");
    // Itera sui link del menu
    menuLinks.forEach(function(link) {
        // Ottieni l'URL del link
        var linkUrl = link.href;
        // Confronta l'URL del link con l'URL della pagina corrente
        if (currentUrl === linkUrl) {
            // Aggiungi la classe 'active' al link corrente
            link.classList.add("active");
        }
    });
});
