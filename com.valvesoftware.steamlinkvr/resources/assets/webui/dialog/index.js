const OnDialogInfo = (sReceivedData) => {
    console.log(sReceivedData);
    const json = JSON.parse(sReceivedData);

    document.getElementById("title").innerText = json["sTitle"];
    document.getElementById("description").innerText = json["sDescription"];
}

const OnMessageReceived = (sMailboxName, sReceivedData) => {
    switch (sMailboxName) {
        case "dialog_info": {
            OnDialogInfo(sReceivedData);
            break;
        }
    }
}

!(() => {
    SetIPCMessageCallback(OnMessageReceived);
})();