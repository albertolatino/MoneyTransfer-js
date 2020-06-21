(function () { // avoid variables ending up in the global scope

    // page components
    let transactionsList, accountsList, wizard, contactList,
        pageOrchestrator = new PageOrchestrator(); // main controller

    window.addEventListener("load", () => {
        if (sessionStorage.getItem("username") == null) {
            window.location.href = "index.html";
        } else {
            pageOrchestrator.start(); // initialize the components
            pageOrchestrator.refresh();
        } // display initial content
    }, false);


    // Constructors of view components

    function PersonalMessage(_username, messagecontainer) {
        this.username = _username;
        this.show = function () {
            messagecontainer.textContent = this.username;
        }
    }

    function AccountsList(_alert, _listcontainer, _listcontainerbody) {
        this.alert = _alert;
        this.listcontainer = _listcontainer;
        this.listcontainerbody = _listcontainerbody;

        this.reset = function () {
            this.listcontainer.style.visibility = "hidden";
        }

        this.show = function (next) {
            var self = this;
            makeCall("GET", "GetAccountData", null,
                function (req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var accountsToShow = JSON.parse(req.responseText);
                            if (accountsToShow.length == 0) {
                                self.alert.textContent = "No accounts yet!";
                                return;
                            }
                            self.update(accountsToShow); // self visible by closure
                            if (next) next(); // show the default element of the list if present
                        }
                    } else {
                        self.alert.textContent = message;
                    }
                }
            );
        };


        this.update = function (accounts) {
            var elem, i, row, accountIdCell, balanceCell, detailsCell, anchor;
            this.listcontainerbody.innerHTML = ""; // empty the table body
            // build updated list
            var self = this;
            accounts.forEach(function (account) { // self visible here, not this
                row = document.createElement("tr");
                accountIdCell = document.createElement("td");
                accountIdCell.textContent = account.accountId;
                row.appendChild(accountIdCell);
                balanceCell = document.createElement("td");
                balanceCell.textContent = account.balance;
                row.appendChild(balanceCell);
                detailsCell = document.createElement("td");
                anchor = document.createElement("a");
                detailsCell.appendChild(anchor);
                linkText = document.createTextNode("Show");
                anchor.appendChild(linkText);
                //anchor.missionid = account.id; // make list item clickable
                anchor.setAttribute('accountid', account.accountId); // set a custom HTML attribute
                anchor.addEventListener("click", (e) => {
                    // dependency via module parameter
                    transactionsList.show(undefined, e.target.getAttribute("accountid")); // the list must know the details container
                }, false);
                anchor.href = "#";
                row.appendChild(detailsCell);
                self.listcontainerbody.appendChild(row);
            });
            this.listcontainer.style.visibility = "visible";

        }

        this.autoclick = function (accountId) {
            var e = new Event("click");
            var selector = "a[accountid='" + accountId + "']";
            var anchorToClick =
                (accountId) ? document.querySelector(selector) : this.listcontainerbody.querySelectorAll("a")[0];
            if (anchorToClick) anchorToClick.dispatchEvent(e);
        }

    }


    function TransactionsList(_alert, _listcontainer, _listcontainerbody) {
        this.alert = _alert;
        this.listcontainer = _listcontainer;
        this.listcontainerbody = _listcontainerbody;

        this.reset = function () {
            this.listcontainer.style.visibility = "hidden";
        }

        this.show = function (next, accountid) {
            var self = this;
            makeCall("GET", "GetAccountDetailsData?accountid=" + accountid, null,
                function (req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var transactionsToShow = JSON.parse(req.responseText);
                            if (transactionsToShow.length == 0) {
                                self.reset();
                                self.alert.textContent = "No transactions yet!";
                                return;
                            }
                            self.update(transactionsToShow); // self visible by closure
                            if (next) next(); // show the default element of the list if present
                        }
                    } else {
                        self.alert.textContent = message;
                    }
                }
            );
        };


        this.update = function (transactions) {
            var elem, i, row, transactionIdCell, dateCell, amountCell, originCell, destinationCell, descriptionCell;
            this.listcontainerbody.innerHTML = ""; // empty the table body
            // build updated list
            var self = this;
            transactions.forEach(function (transaction) { // self visible here, not this
                row = document.createElement("tr");
                transactionIdCell = document.createElement("td");
                transactionIdCell.textContent = transaction.transactionId;
                row.appendChild(transactionIdCell);

                dateCell = document.createElement("td");
                dateCell.textContent = transaction.date;
                row.appendChild(dateCell);

                amountCell = document.createElement("td");
                amountCell.textContent = transaction.amount;
                row.appendChild(amountCell);

                originCell = document.createElement("td");
                originCell.textContent = transaction.originId;
                row.appendChild(originCell);

                destinationCell = document.createElement("td");
                destinationCell.textContent = transaction.destinationId;
                row.appendChild(destinationCell);

                descriptionCell = document.createElement("td");
                descriptionCell.textContent = transaction.description;
                row.appendChild(descriptionCell);

                self.listcontainerbody.appendChild(row);
            });
            this.listcontainer.style.visibility = "visible";

        }

        //todo quando facciamo transaction autoclick su "details" di account che fa transaction per refresh lista di transactions
        this.autoclick = function (accountId) {
            var e = new Event("click");
            var selector = "a[accountid='" + accountId + "']";
            var anchorToClick =
                (accountId) ? document.querySelector(selector) : this.listcontainerbody.querySelectorAll("a")[0];
            if (anchorToClick) anchorToClick.dispatchEvent(e);
        }

    }

    function Wizard(wizardId, alert) {

        this.wizard = wizardId;
        this.alert = alert;

        this.registerEvents = function (orchestrator) {
            // Manage previous and next buttons
            Array.from(this.wizard.querySelectorAll("input[type='button'].next,  input[type='button'].prev")).forEach(b => {
                b.addEventListener("click", (e) => { // arrow function preserve the visibility of this
                    let eventfieldset = e.target.closest("fieldset"),
                        valid = true;
                    if (e.target.className == "next") {
                        for (i = 0; i < eventfieldset.elements.length; i++) {
                            if (!eventfieldset.elements[i].checkValidity()) {
                                eventfieldset.elements[i].reportValidity();
                                valid = false;
                                break;
                            }
                        }
                    }
                    if (valid) {
                        this.changeStep(e.target.parentNode, (e.target.className === "next") ? e.target.parentNode.nextElementSibling : e.target.parentNode.previousElementSibling);
                    }
                }, false);
            });

            // Manage submit button
            this.wizard.querySelector("input[type='button'].submit").addEventListener('click', (e) => {
                var eventfieldset = e.target.closest("fieldset"),
                    valid = true;
                for (i = 0; i < eventfieldset.elements.length; i++) {
                    if (!eventfieldset.elements[i].checkValidity()) {
                        eventfieldset.elements[i].reportValidity();
                        valid = false;
                        break;
                    }
                }

                if (valid) {
                    var self = this;
                    makeCall("POST", 'CreateTransaction', e.target.closest("form"),
                        function (req) {
                            if (req.readyState == XMLHttpRequest.DONE) {
                                var message = req.responseText; // error message or mission id
                                if (req.status == 200) {
                                    document.getElementById("addtocontacts").style.visibility = "visible";
                                    orchestrator.refresh(message); // id of the new mission passed
                                } else {
                                    self.alert.textContent = message;
                                    self.reset();
                                }
                            }
                        }
                    );
                }
            });
            // Manage cancel button
            this.wizard.querySelector("input[type='button'].cancel").addEventListener('click', (e) => {
                e.target.closest('form').reset();
                this.reset();
            });
        };

        this.reset = function () {
            let fieldset1 = document.querySelector("#first");
            let fieldset2 = document.querySelector("#second");

            fieldset1.hidden = false;
            fieldset2.hidden = true;
        }

        this.changeStep = function (origin, destination) {
            origin.hidden = true;
            destination.hidden = false;
        }
    }


    function ContactList(_alert) {

        this.alert = _alert;
        const self = this;

        this.registerEvents = function (orchestrator) {
            registerAddContactEvent(orchestrator, "true");
            registerAddContactEvent(orchestrator, "false");
        }

        function registerAddContactEvent(orchestrator, boolString) {

            document.getElementById(boolString + "_button").addEventListener('click', () => {

                makeCall("POST", 'AddToContacts?addToContacts=' + boolString, null,
                    function (req) {
                        if (req.readyState === XMLHttpRequest.DONE) {
                            var message = req.responseText; // error message or mission id
                            if (req.status === 200) {
                                document.getElementById("addtocontacts").style.visibility = "hidden";
                                //orchestrator.refresh(message); //TODO AL MAX FAI REFRESH PERCHé VUOI FAR VEDERE NUOVA LISTA CONTATTI
                                self.alert.textContent = "User successfully added!"

                            } else {
                                self.alert.textContent = message;
                                self.reset();
                            }
                        }
                    }
                );
            });
        }


        this.getContacts = function () {
            const self = this;
            makeCall("GET", "AddToContacts", null,
                function (req) {
                    const message = req.responseText;
                    if (req.readyState === 4) {
                        if (req.status === 200) {
                            const contactsObj = JSON.parse(req.responseText);
                            if(contactsObj.length > 0) {
                                var contacts = [];

                                for(let i = 0; i < contactsObj.length; i++) {
                                    contacts.push(contactsObj[i].contactUsername);
                                }
                                var usernameInput = document.getElementById("recipient-username");
                                autocomplete(usernameInput, contacts);
                            }

                        }
                    } else {
                        //self.alert.textContent = message;
                    }
                }
            );
        };


        this.getContacts();

        function autocomplete(inp, arr) {
            /*the autocomplete function takes two arguments,
            the text field element and an array of possible autocompleted values:*/
            var currentFocus;
            /*execute a function when someone writes in the text field:*/
            inp.addEventListener("input", function (e) {
                var a, b, i, val = this.value;
                /*close any already open lists of autocompleted values*/
                closeAllLists();
                if (!val) {
                    return false;
                }
                currentFocus = -1;
                /*create a DIV element that will contain the items (values):*/
                a = document.createElement("DIV");
                a.setAttribute("id", this.id + "autocomplete-list");
                a.setAttribute("class", "autocomplete-items");

                /*append the DIV element as a child of the autocomplete container:*/
                this.parentNode.appendChild(a);
                /*for each item in the array...*/
                for (i = 0; i < arr.length; i++) {
                    /*check if the item starts with the same letters as the text field value:*/
                    if (arr[i].substr(0, val.length).toUpperCase() === val.toUpperCase()) {
                        /*create a DIV element for each matching element:*/
                        b = document.createElement("DIV");
                        /*make the matching letters bold:*/
                        b.innerHTML = "<strong>" + arr[i].substr(0, val.length) + "</strong>";
                        b.innerHTML += arr[i].substr(val.length);
                        /*insert a input field that will hold the current array item's value:*/
                        b.innerHTML += "<input type='hidden' value='" + arr[i] + "'>";
                        /*execute a function when someone clicks on the item value (DIV element):*/
                        b.addEventListener("click", function (e) {
                            /*insert the value for the autocomplete text field:*/
                            inp.value = this.getElementsByTagName("input")[0].value;
                            /*close the list of autocompleted values,
                            (or any other open lists of autocompleted values:*/
                            closeAllLists();
                        });
                        a.appendChild(b);
                    }
                }
            });
            /*execute a function presses a key on the keyboard:*/
            inp.addEventListener("keydown", function (e) {
                var x = document.getElementById(this.id + "autocomplete-list");
                if (x) x = x.getElementsByTagName("div");
                if (e.keyCode == 40) {
                    /*If the arrow DOWN key is pressed,
                    increase the currentFocus variable:*/
                    currentFocus++;
                    /*and and make the current item more visible:*/
                    addActive(x);
                } else if (e.keyCode == 38) { //up
                    /*If the arrow UP key is pressed,
                    decrease the currentFocus variable:*/
                    currentFocus--;
                    /*and and make the current item more visible:*/
                    addActive(x);
                } else if (e.keyCode == 13) {
                    /*If the ENTER key is pressed, prevent the form from being submitted,*/
                    e.preventDefault();
                    if (currentFocus > -1) {
                        /*and simulate a click on the "active" item:*/
                        if (x) x[currentFocus].click();
                    }
                }
            });

            function addActive(x) {
                /*a function to classify an item as "active":*/
                if (!x) return false;
                /*start by removing the "active" class on all items:*/
                removeActive(x);
                if (currentFocus >= x.length) currentFocus = 0;
                if (currentFocus < 0) currentFocus = (x.length - 1);
                /*add class "autocomplete-active":*/
                x[currentFocus].classList.add("autocomplete-active");
            }

            function removeActive(x) {
                /*a function to remove the "active" class from all autocomplete items:*/
                for (var i = 0; i < x.length; i++) {
                    x[i].classList.remove("autocomplete-active");
                }
            }

            function closeAllLists(elmnt) {
                /*close all autocomplete lists in the document,
                except the one passed as an argument:*/
                var x = document.getElementsByClassName("autocomplete-items");
                for (var i = 0; i < x.length; i++) {
                    if (elmnt != x[i] && elmnt != inp) {
                        x[i].parentNode.removeChild(x[i]);
                    }
                }
            }

            /*execute a function when someone clicks in the document:*/
            document.addEventListener("click", function (e) {
                closeAllLists(e.target);
            });
        }

    }


    function PageOrchestrator() {
        const alertContainer = document.getElementById("id_alert");
        this.start = function () {
            let personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
                document.getElementById("id_username"));
            personalMessage.show();

            accountsList = new AccountsList(
                alertContainer,
                document.getElementById("id_listcontainer"),
                document.getElementById("id_listcontainerbody"));


            transactionsList = new TransactionsList(
                alertContainer,
                document.getElementById("id_transactionscontainer"),
                document.getElementById("id_transactionsbody"));


            wizard = new Wizard(document.getElementById("id_createtransactionform"), alertContainer);
            wizard.registerEvents(this);

            contactList = new ContactList(alertContainer);
            contactList.registerEvents(this);

            document.querySelector("a[href='Logout']").addEventListener('click', () => {
                window.sessionStorage.removeItem('username');
            })
        };


        this.refresh = function (currentAccount) {
            alertContainer.textContent = "";
            accountsList.reset();
            accountsList.show(function () {
                accountsList.autoclick(currentAccount);
            }, currentAccount); // closure preserves visibility of this
            wizard.reset();
        };
    }


})();
