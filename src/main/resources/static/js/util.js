// =============================
// Evidenzia dinamicamente il link attivo del menu
// =============================
function highlightActiveMenuLink() {
    const currentUrl = window.location.pathname; // ottiene la path attuale
    const menuLinks = document.querySelectorAll(".navbar-nav .nav-link"); // seleziona tutti i link del menu

    menuLinks.forEach(link => {
        const linkUrl = new URL(link.href).pathname; // path del link
        // Se l'URL corrente inizia con quello del link (per sottopagine)
        if (currentUrl === '/' && linkUrl === '/') {
            // caso speciale per root
            link.classList.add("active");
        } else if (currentUrl.startsWith(linkUrl) && linkUrl !== '/') {
            link.classList.add("active");
        } else {
            link.classList.remove("active");
        }
    });
}

// Inizializza al caricamento del DOM
document.addEventListener("DOMContentLoaded", highlightActiveMenuLink);


// =============================
// Mostra / nasconde overlay di caricamento
// =============================
function showLoadingOverlay() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) overlay.style.display = 'flex';
}

function hideLoadingOverlay() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) overlay.style.display = 'none';
}


// =============================
// Registrazione Service Worker per PWA
// =============================
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
