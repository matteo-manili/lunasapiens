
    function isPresentCookieDisabledGoogleAnalytics() {
        const targetCookieName = 'cookie_disable_google_analytics'; // Nome del cookie da verificare
        // Ottieni tutti i cookie come una stringa
        const cookies = document.cookie;
        // Dividi i cookie in un array usando il delimitatore "; "
        const cookieArray = cookies.split('; ');
        // Controlla ogni cookie per vedere se il nome corrisponde
        for (let i = 0; i < cookieArray.length; i++) {
            const [name, value] = cookieArray[i].split('=');
            if (name === targetCookieName) {
                return true; // Cookie trovato
            }
        }
        return false; // Cookie non trovato
    }

    if( isPresentCookieDisabledGoogleAnalytics() ){
        window['ga-disable-G-XGFTKCH8TD'] = true; // Consenso negato
        //console.info('google analytics NON ATTIVO');
    }else{
        window['ga-disable-G-XGFTKCH8TD'] = false; // Consenso approvato
        window.dataLayer = window.dataLayer || [];
        function gtag(){dataLayer.push(arguments);}
        gtag('js', new Date());
        gtag('config', 'G-XGFTKCH8TD');
        //console.info('google analytics ATTIVO');
    }

        // Funzioni per inviare l'evento personalizzato a GA4
    function googleTrackFormSubmit(submitFormName) {
        if(isPresentCookieDisabledGoogleAnalytics() == false){
            gtag('event', submitFormName, {
            'event_category': 'Form',
            'value': 1 });
            console.info('eseguo googleTrackFormSubmit: '+submitFormName);
        }
    }

