const HandleHideCancelThenShowAfterMessage = (sReceivedData) => {
    const json = JSON.parse(sReceivedData);

    const cancelButtonElement = document.getElementById('button-abort-connection');
    cancelButtonElement.hidden = true;

    setTimeout(() => {
        cancelButtonElement.hidden = false;
    }, json?.timeoutMs);
}

const HandleHideCancelMessage = (sReceivedData) => {
    const cancelButtonElement = document.getElementById('button-abort-connection');
    cancelButtonElement.hidden = true;
}

const HandleTitleSetMessage = (sReceivedData) => {
    const json = JSON.parse(sReceivedData);
    document.getElementById('title').innerText = json?.sTitle;
    document.getElementById('title-description').innerText = json?.sDescription;
}

const OnMessageReceived = (sMailboxName, sReceivedData) => {
    switch (sMailboxName) {
        case "set_title": {
            HandleTitleSetMessage(sReceivedData);
            break;
        }

        case "hide_cancel": {
            HandleHideCancelMessage(sReceivedData);
            break;
        }
        case "hide_cancel_then_show_after_ms": {
            HandleHideCancelThenShowAfterMessage(sReceivedData);
            break;
        }
    }
}

!(() => {
    SetIPCMessageCallback(OnMessageReceived);
})();

const AbortConnection = () => {
    SendIPCMessage("request_abort", {});
}