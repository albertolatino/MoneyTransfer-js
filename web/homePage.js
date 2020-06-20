(function () { // avoid variables ending up in the global scope

    // page components
    var transactionsList, accountsList, wizard,
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
                linkText = document.createTextNode("Details");
                anchor.appendChild(linkText);
                //anchor.missionid = account.id; // make list item clickable
                anchor.setAttribute('accountid', account.accountId); // set a custom HTML attribute
                anchor.addEventListener("click", (e) => {
                    // dependency via module parameter
                    transactionsList.show(undefined,e.target.getAttribute("accountid")); // the list must know the details container
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
        // minimum date the user can choose, in this case now and in the future
        var now = new Date(),
            formattedDate = now.toISOString().substring(0, 10);
        this.wizard = wizardId;
        this.alert = alert;

        this.wizard.querySelector('input[type="date"]').setAttribute("min", formattedDate);

        this.registerEvents = function (orchestrator) {
            // Manage previous and next buttons
            Array.from(this.wizard.querySelectorAll("input[type='button'].next,  input[type='button'].prev")).forEach(b => {
                b.addEventListener("click", (e) => { // arrow function preserve the
                    // visibility of this
                    var eventfieldset = e.target.closest("fieldset"),
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
                    makeCall("POST", 'CreateMission', e.target.closest("form"),
                        function (req) {
                            if (req.readyState == XMLHttpRequest.DONE) {
                                var message = req.responseText; // error message or mission id
                                if (req.status == 200) {
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
            var fieldsets = document.querySelectorAll("#" + this.wizard.id + " fieldset");
            fieldsets[0].hidden = false;
            fieldsets[1].hidden = true;
            fieldsets[2].hidden = true;

        }

        this.changeStep = function (origin, destination) {
            origin.hidden = true;
            destination.hidden = false;
        }
    }

    function PageOrchestrator() {
        var alertContainer = document.getElementById("id_alert");
        this.start = function () {
            personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
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


            wizard = new Wizard(document.getElementById("id_createmissionform"), alertContainer);
            wizard.registerEvents(this);

            document.querySelector("a[href='Logout']").addEventListener('click', () => {
                window.sessionStorage.removeItem('username');
            })
        };


        this.refresh = function (currentAccount) {
            alertContainer.textContent = "";
            accountsList.reset();
            accountsList.show(function () {
                accountsList.autoclick(currentAccount);
            },currentAccount); // closure preserves visibility of this
            wizard.reset();
        };
    }
})();
