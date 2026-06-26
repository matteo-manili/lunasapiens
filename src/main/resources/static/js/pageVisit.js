
function initPageVisit(csrf) {

    // *************** start visit ***************
    let visitStarted = false;
    let visitTimer = setTimeout(startVisit, 10000);

    function startVisit() {

        if (visitStarted) return;

        visitStarted = true;
        clearTimeout(visitTimer);

        fetch('/start-visit', {
            method: 'POST',
            keepalive: true,
            headers: {
                'Content-Type': 'application/json', 'X-XSRF-TOKEN': csrf
            },
            body: JSON.stringify({ path: window.location.pathname })
        }).catch(() => {});
    }

    window.addEventListener("scroll", startVisit, { once: true });
    window.addEventListener("click", startVisit, { once: true });
    window.addEventListener("touchstart", startVisit, { once: true });
    window.addEventListener("keydown", startVisit, { once: true });


    // *************** heartbeat leggero ***************
    setInterval(() => {

        fetch('/page-activity', {
            method: 'POST',
            keepalive: true,
            headers: {
                'X-XSRF-TOKEN': csrf
            }
        }).catch(() => {});

    }, 15000);


} // <-- FINE initPageVisit