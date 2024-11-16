const OnPreflightResultReceived = (sReceivedData) => {
    const json = JSON.parse(sReceivedData);

    const preflightResultsElement = document.getElementById('preflight-results');
    preflightResultsElement.innerHTML = ``;

    document.getElementById(`preflight-continue`).hidden = json?.bShouldNotShowContinue;

    for (const preflightCheck of json?.infos) {
        let stateText;
        switch (preflightCheck.result) {
            case 1: {
                stateText = `✅`;
                break;
            }
            case 2: {
                stateText = `⚠️`;
                break;
            }
            case 3: {
                stateText = `❗`;
                break;
            }
            case 4: {
                stateText = `🚫`;
                break;
            }

            default: {
                stateText = `❓`;
            }
        }

        const preflightResultTemplate = document.getElementById('preflight-result-template');
        const preflightResultElement = preflightResultTemplate.content.cloneNode(true);

        preflightResultElement.querySelector('.result-type').innerText = stateText;
        preflightResultElement.querySelector('.result-title-message').innerText = preflightCheck.title;
        preflightResultElement.querySelector('.result-description-message').innerText = preflightCheck.description;

        if (preflightCheck.sIgnoreWarningAppPath) {
            const ignoreButtonElement = preflightResultElement.querySelector('.result-ignore-button');
            ignoreButtonElement.onclick = () => {
                UpdateAppPath(preflightCheck.sIgnoreWarningAppPath, true);
                SendIPCMessage("request_preflight_check_update", {});
            }
            ignoreButtonElement.hidden = false;
        }

        preflightResultsElement.appendChild(preflightResultElement);
    }
}

const OnContinueClicked = () => {
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