/**
 * Registration management
 */

(function () { // avoid variables ending up in the global scope

    document.getElementById("registrationButton").addEventListener('click', (e) => {
        var form = e.target.closest("form");
        if (form.checkValidity()) {
            makeCall("POST", 'RegisterUser', e.target.closest("form"),
                function (req) {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        var message = req.responseText;
                        switch (req.status) {

                            case 200:
                                sessionStorage.setItem('username', message);
                                document.getElementById("successfulRegistration").style.visibility = "visible";
                                break;
                            case 400: // bad request
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

    document.getElementById("loginLink").addEventListener('click', () => {
        window.location.href = "index.html";
    });

})();