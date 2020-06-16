/**
 * Registration management
 */

(function() { // avoid variables ending up in the global scope

    document.getElementById("registrationbutton").addEventListener('click', (e) => {
        var form = e.target.closest("form");
        if (form.checkValidity()) {
            makeCall("POST", 'RegisterUser', e.target.closest("form"),
                function(req) {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        var message = req.responseText;
                        switch (req.status) {

                            //todo accordare con fra lo status e i messaggi che settera nella servlet in base ai vari casi
                            case 200:
                                sessionStorage.setItem('username', message);
                                window.location.href = "HomeCS.html";
                                break;
                            case 400: // bad request
                                document.getElementById("errormessage").textContent = message;
                                break;
                            case 401: // unauthorized
                                document.getElementById("errormessage").textContent = message;
                                break;
                            case 500: // server error
                                document.getElementById("errormessage").textContent = message;
                                break;
                        }
                    }
                }
            );
        } else {
            form.reportValidity();
        }
    });

})();