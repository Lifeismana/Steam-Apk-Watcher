let webMessagePort;

let IPCCallback = (f) => {
    console.log("unhandled callback: " + f.data);
};

const SetIPCMessageCallback = (callback) => {
    IPCCallback = callback;
}

let OnConnectCallback = () => {
}

const SetOnConnectCallback = (callback) => {
    OnConnectCallback = callback;
}

const UpdateAppPath = (sPath, value) => {
    SendIPCMessage("app_path_updated", {
        [sPath]: value
    });
}

const UpdateAppPaths = (paths) => {
    SendIPCMessage("app_path_updated", paths);
}

const SendHaptic = () => {
    SendIPCMessage("send_haptic", {
    });
}

const PlaySound = (path) => {
    const audio = new Audio(path);
    audio.play();
}


function AddClass( element, className )
{
    element.classList.add(className);
}
function RemoveClass( element, className )
{
    element.classList.remove(className);
}

let over_elements = [];
const OnLaserIntersectionMessage = (sReceivedData) => {
    const json = JSON.parse(sReceivedData);

    const width = window.innerWidth;
    const height = window.innerHeight;

    const x = Math.round(json.x * width);
    const y = Math.round(json.y * height);

    var not_over_elements = [];
    for (i = 0; i < over_elements.length; i++) {
        not_over_elements[i] = over_elements[i];
    }
    const elements = document.elementsFromPoint(x, y);
    for (const element of elements) {
        if (element.nodeName === "BUTTON" || element.nodeName === "button" ) {
            
            const current_index = over_elements.indexOf(element);
            if (current_index == -1) 
            {
                over_elements.push(element);
                AddClass(element, "button-hover");
                SendHaptic();
            }
            else
            {
                const over_index = not_over_elements.indexOf(element);
                not_over_elements.splice(over_index, 1);
            }
        }
    }

    for (const element of not_over_elements)
    {
        RemoveClass(element, "button-hover");
        const over_index = over_elements.indexOf(element);
        over_elements.splice(over_index, 1);
    }

    if (json.click) {
        let bDidClickSomething = false;
        const elements = document.elementsFromPoint(x, y);
        for (const element of elements) {
            const lElement = element.nodeName?.toLowerCase();
            if (lElement === "button") {
                element.click();
                bDidClickSomething = true;
                console.log("click: " + element.id);
                SendHaptic();
                AddClass(element, "button-active");
                setTimeout(() => {
                    RemoveClass(element, "button-active");
                }, 200);
            }

            if(lElement === "input" && element.type === "checkbox") {
                element.checked = !element.checked;
                if(element.checked) {
                    element.setAttribute("checked", "checked");
                } else {
                    element.removeAttribute("checked");
                }

                let event = new Event('change');
                element.dispatchEvent(event);
                bDidClickSomething = true;
                SendHaptic();
            }
        }
        if (!bDidClickSomething)
        {
            console.log("couldn't find anything to click");
        }

        PlaySound(bDidClickSomething ? "../sounds/activation.wav" : "../sounds/activation_change_fail.wav");
    }

    {
        const pointerPipElement = document.getElementById('pointer-pip');
        if (pointerPipElement) {
            if (json.click) {
                document.getElementById('pointer-pip').style.fill = 'lightblue';
            } else {
                document.getElementById('pointer-pip').style.fill = 'white';
            }
        }
    }
}

const OnAppPathsMessage = (sReceivedData) => {
    const json = JSON.parse(sReceivedData);

    for (const [sPath, value] of Object.entries(json)) {
        const elements = document.querySelectorAll(`[data-appPath="${sPath}"]`);

        elements.forEach((element) => {
            element.innerText = value;
        });
    }
}

let jsonCurrentLocalization = {};

const Localize = (sString) => {
    if (sString == undefined || sString.charAt(0) !== '#') {
        return sString;
    }

    const vSplitted = sString.split('{');

    const sKey = vSplitted[0];
    let sLocalizedRaw = jsonCurrentLocalization[sKey.substring(1)];

    let sLocalized = sLocalizedRaw;
    if(!sLocalized) {
        return sString;
    }

    if(vSplitted.length > 1) {
        const onlyValues = vSplitted[1]?.split('}').shift();
        const vValues = onlyValues.split(',');
        for(let i = 0; i < vValues.length; i++) {
            let search = "%" + i + "$s";
            sLocalized = sLocalizedRaw.replace(search, vValues[i]);
        }
    }

    return sLocalized;
}

const UpdateLocalization = () => {
    const localizeElements = document.querySelectorAll(`[data-localize]`);

    localizeElements.forEach(e => {
        const sLocalizeKey = e.dataset?.localize;
        e.innerText = Localize(sLocalizeKey);
    });
}

const OnLocaleMessage = (sReceivedData) => {
    const json = JSON.parse(sReceivedData);

    jsonCurrentLocalization = json?.localization;
    UpdateLocalization();
}

const HandleMessageInternal = (data) => {
    const sMailboxName = data.substring(0, data.indexOf(' '));
    const sReceivedData = data.substring(data.indexOf(' ') + 1);

    switch (sMailboxName) {
        case "laser_intersection": {
            OnLaserIntersectionMessage(sReceivedData);
            break;
        }

        case "app_paths": {
            OnAppPathsMessage(sReceivedData);
            break;
        }

        case "locale": {
            OnLocaleMessage(sReceivedData);
            break;
        }

        default: {
            IPCCallback(sMailboxName, sReceivedData);
            break;
        }
    }

}

const SendIPCMessage = (sMailbox, objData) => {
    const sData = sMailbox + " " + JSON.stringify(objData);
    console.log("IPC: " + sData);
    webMessagePort.postMessage(sData);
}

onmessage = (e) => {
    console.log("onmessage: Received initial webview message");
    webMessagePort = e.ports[0];

    OnConnectCallback();

    webMessagePort.onmessage = (f) => {
        HandleMessageInternal(f.data);
    }
}

!(() => {
    UpdateLocalization();
})();

const HandleWifiMessage = (sReceivedData) => {
    const json = JSON.parse(sReceivedData);

    var text = Localize(json?.sText);

    if ( json?.bIs2g )
    {
        text = text + Localize("#Wifi2GNetwork");
    }

    document.getElementById('wifi').innerText = text;
}

const HandleErrorcodeMessage = (sReceivedData) => {
    const json = JSON.parse(sReceivedData);

    var text = Localize(json?.sText);

    document.getElementById('error-code').innerText = text;
}

const AbortConnection = () => {
    SendIPCMessage("request_abort", {});
}

const ResumeConnection = () => {
    SendIPCMessage("request_continue", {});
}