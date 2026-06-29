
function initPageVisit(csrf) {

    // *************** START VISIT ***************
    // Evita che la visita venga registrata più volte nella stessa pagina
    let visitStarted = false;
    // Se l'utente rimane sulla pagina almeno 10 secondi,
    // registriamo comunque la visita anche senza interazioni
    let visitTimer = setTimeout(startVisit, 10000);

    // Funzione che avvia la registrazione della visita
    function startVisit() {
        // Se la visita è già stata registrata esce subito
        if (visitStarted) return;
        // Segna la visita come già avviata
        visitStarted = true;
        // Annulla il timer dei 10 secondi perché la visita è partita prima
        clearTimeout(visitTimer);
        // Invio al backend della pagina visitata
        fetch('/start-visit', {
            method: 'POST',
            // Permette di completare la richiesta anche se l'utente cambia pagina
            keepalive: true,
            headers: {
                'Content-Type': 'application/json', 'X-XSRF-TOKEN': csrf
            },
            // Invia il percorso della pagina corrente e il referrer
            body: JSON.stringify({ path: window.location.pathname, referer: document.referrer })
        // Ignora eventuali errori perché il tracking non deve bloccare il sito
        }).catch(() => {});
    }

    // Se l'utente interagisce con la pagina,
    // la visita viene registrata subito senza aspettare i 10 secondi
    // Scroll della pagina
    window.addEventListener("scroll", startVisit, { once: true });
    // Click del mouse
    window.addEventListener("click", startVisit, { once: true });
    // Primo tocco su smartphone/tablet
    window.addEventListener("touchstart", startVisit, { once: true });
    // Tasto premuto
    window.addEventListener("keydown", startVisit, { once: true });


    // *************** HEARTBEAT LEGGERO ***************
    // Invia periodicamente un segnale al server per indicare
    // che l'utente è ancora sulla pagina.
    // Serve ad aggiornare il campo "lastSeen" della visita.
    setInterval(() => {

        fetch('/page-activity', {
            method: 'POST',
            // Prova a completare la richiesta anche se la pagina
            // viene chiusa o cambiata durante l'invio
            keepalive: true,
            headers: {
                // Token CSRF per permettere la richiesta protetta da Spring Security
                'X-XSRF-TOKEN': csrf
            }
        // Gli errori del tracking vengono ignorati: una mancata chiamata heartbeat non deve influire sulla navigazione
        }).catch(() => {});
        // Esegue il controllo ogni 15 secondi
    }, 15000);


} // <-- FINE initPageVisit