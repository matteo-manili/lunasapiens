/**
 * ✅ Tracciamento conversione Google Ads (Domanda chat)
 * @param url
 * @returns {boolean}
 */
function gtag_report_conversion(url) {
    var callback = function () {
        if (typeof(url) != 'undefined') {
            window.location = url;
        }
    };
    gtag('event', 'conversion', {
        'send_to': 'AW-1011317208/MmU3CLiyra0bENjzneID',
        'event_callback': callback
    });
    return false;
}
