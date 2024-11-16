let checkboxStates = {};

const OnPreflightResultReceived = (sReceivedData) => {
    const json = JSON.parse(sReceivedData);

    const preflightResultsElement = document.getElementById('preflight-results');
    preflightResultsElement.innerHTML = ``;

    document.getElementById(`preflight-continue`).hidden = json?.bShouldNotShowContinue;

    for (const preflightCheck of json?.infos) {
        let stateText;
        switch (preflightCheck.result) {
            case 1: {
                stateText = '../images/preflight-check-succeed.svg';
                break;
            }
            case 2: {
                stateText = '../images/preflight-check-info.svg';
                break;
            }
            case 3: {
                stateText = '../images/preflight-check-warning.svg';
                break;
            }
            case 4: {
                stateText = '../images/preflight-check-invalid.svg';
                break;
            }
            case 5: {
                stateText = '../images/preflight-check-fail.svg';
                break;
            }

            default: {
                stateText = '../images/preflight-check-question.svg';
            }
        }

        const preflightResultTemplate = document.getElementById('preflight-result-template');
        const preflightResultElement = preflightResultTemplate.content.cloneNode(true);

        preflightResultElement.querySelector('.result-type').src = stateText;
        preflightResultElement.querySelector('.result-title-message').innerText = Localize(preflightCheck.title);
        preflightResultElement.querySelector('.result-description-message').innerText = Localize(preflightCheck.description);

        if(preflightCheck?.ignorePath) {
            preflightResultElement.querySelector('.preflight-ignore').hidden = false;

            const checkbox = preflightResultElement.querySelector('.preflight-ignore-checkbox');
            checkbox.name = preflightCheck.ignorePath;
            checkbox.checked = checkboxStates[preflightCheck.ignorePath];
            if(checkbox.checked) {
                checkbox.setAttribute('checked', 'checked');
            }

            checkbox.addEventListener('change', (e) => {
                checkboxStates[preflightCheck.ignorePath] = !!e.currentTarget.checked;
            });

            preflightResultElement.querySelector('.preflight-ignore-text').innerText = Localize("#StreamPreflight_IgnoreForNetwork");
        } else {
            preflightResultElement.querySelector('.preflight-ignore').classList += 'hidden';
        }

        preflightResultsElement.appendChild(preflightResultElement);
    }
}

const OnContinueClicked = () => {
    UpdateAppPaths(checkboxStates);
    SendIPCMessage("continue", {});
}

const OnExitClicked = () => {
    SendIPCMessage("exit", {});
}

const OnMessageReceived = (sMailboxName, sReceivedData) => {
    switch (sMailboxName) {
        case "preflight_results": {
            OnPreflightResultReceived(sReceivedData);
            break;
        }
    }
}

!(() => {
    SetIPCMessageCallback(OnMessageReceived);
})();