var setMessageTimeoutID = -1;
var hideDelayShowTimeoutID = -1;

const HandleHideCancelMessage = (sReceivedData) => {
    const cancelButtonElement = document.getElementById('button-abort-connection');
    cancelButtonElement.hidden = true;
}

const HandleShowCancelMessage = (sReceivedData) => {
    const cancelButtonElement = document.getElementById('button-abort-connection');
    cancelButtonElement.hidden = false;
}

const HandleHideCancelThenShowAfterMessage = (sReceivedData) => {
    const json = JSON.parse(sReceivedData);
    const fDelay = json?.fDelay;

    const cancelButtonElement = document.getElementById('button-abort-connection');
    cancelButtonElement.hidden = true;
    
    if (hideDelayShowTimeoutID != -1)
    {
        clearTimeout(hideDelayShowTimeoutID);
        hideDelayShowTimeoutID = -1;
    }
    
    hideDelayShowTimeoutID = setTimeout(() => {
        cancelButtonElement.hidden = false;
    }, fDelay);
}

const HandleTitleSetMessage = (sReceivedData) => {
    const json = JSON.parse(sReceivedData);
    const fDelay = json?.fDelay;

    //document.getElementById('content').style.display = "none";
    //document.getElementById('loader-animation').style.display = "flex";
    document.getElementById('title').innerText = Localize(json?.sTitle);
    document.getElementById('title-description').innerText = Localize(json?.sDescription);

    if (setMessageTimeoutID != -1)
    {
        clearTimeout(setMessageTimeoutID);
        setMessageTimeoutID = -1;
    }

    if (fDelay > 0)
    {
        const json = JSON.parse(sReceivedData);

        setMessageTimeoutID = setTimeout(() => {
            //document.getElementById('content').style.display = "flex";
            //document.getElementById('loader-animation').style.display = "none";

            document.getElementById('title').innerText = Localize(json?.sDelayedTitle);
            document.getElementById('title-description').innerText = Localize(json?.sDelayedDescription);
        }, fDelay);
    }
    else
    {
        //document.getElementById('content').style.display = "flex";
        //document.getElementById('loader-animation').style.display = "none";
    }
}

const HandleHeaderSetHidden = (sReceivedData) => {
    const json = JSON.parse(sReceivedData);
    document.getElementById('header').hidden = json?.hidden;
}

const HandleSetSpinnerMessage = (sReceivedData) => {
    const json = JSON.parse(sReceivedData);
    if (json?.bShow)
        document.getElementById('loader-animation').style.display = "flex";
    else
        document.getElementById('loader-animation').style.display = "none";
}

const OnMessageReceived = (sMailboxName, sReceivedData) => {
    switch (sMailboxName) {
        case "set_wifi": {
            HandleWifiMessage(sReceivedData);
            break;
        }
        case "set_errorcode": {
            HandleErrorcodeMessage(sReceivedData);
            break;
        }
        case "set_title": {
            HandleTitleSetMessage(sReceivedData);
            break;
        }
        case "set_header_hidden": {
            HandleHeaderSetHidden(sReceivedData);
            break;
        }
        case "hide_cancel": {
            HandleHideCancelMessage(sReceivedData);
            break;
        }
        case "show_cancel": {
            HandleShowCancelMessage(sReceivedData);
            break;
        }
        case "hide_cancel_then_show_after_ms": {
            HandleHideCancelThenShowAfterMessage(sReceivedData);
            break;
        }
        case "set_spinner": {
            HandleSetSpinnerMessage(sReceivedData);
            break;
        }
    }
}

!(() => {
    SetIPCMessageCallback(OnMessageReceived);
})();