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

    document.getElementById("repeat_password_id").addEventListener('keyup', () => {

        if (document.getElementById('password_id').value ===
            document.getElementById('repeat_password_id').value) {
            document.getElementById('message').style.color = 'green';
            document.getElementById('message').innerHTML = 'Matching';
            //document.getElementById('registrationButton').disabled = false;

        } else {
            document.getElementById('message').style.color = 'red';
            document.getElementById('message').innerHTML = 'Not matching';
            //document.getElementById('registrationButton').disabled = true;
        }


    });

    document.getElementById("email_id").addEventListener('keyup', () => {

        const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        let email = document.getElementById('email_id').value;
        if(re.test(String(email).toLowerCase())){
            document.getElementById('validate_email_id').style.color = 'green';
            document.getElementById('validate_email_id').innerHTML = 'Valid Email';

        }else{
            document.getElementById('validate_email_id').style.color = 'red';
            document.getElementById('validate_email_id').innerHTML = 'Invalid Email';
        }
    });



})();