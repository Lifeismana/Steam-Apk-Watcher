let sCurrentPage = "ui-content-hosts";

const ShowNoHostsFoundElement = () => {
    const noHostsFoundTemplate = document.querySelector("#no-available-hosts-template");
    const noHostsFoundElement = noHostsFoundTemplate.content.cloneNode(true);

    document.querySelector("#available-hosts").appendChild(noHostsFoundElement);
}

const HideNoHostsFoundElement = () => {
    const availableHostsElement = document.querySelector("#available-hosts");

    const noHostsFoundElement = availableHostsElement.querySelector("#no-available-hosts");
    if (noHostsFoundElement) {
        availableHostsElement.removeChild(noHostsFoundElement);
    }
}

const ClearHosts = () => {
    document.querySelector("#available-hosts").innerHTML = "";
}

const AddHost = (sHostName, sIp) => {
    const availableHostsElement = document.querySelector("#available-hosts");
    const availableHostTemplate = document.querySelector("#available-host-template");

    const newAvailableHostElement = availableHostTemplate.content.cloneNode(true);
    newAvailableHostElement.querySelector(".host-name").textContent = sHostName + " - " + sIp;
    newAvailableHostElement.querySelector(".host-connect").onclick = () => {
        SendIPCMessage("host_connect", {
            sUDPIp: sIp,
        });
    }

    availableHostsElement.appendChild(newAvailableHostElement);
}

const OpenTCPConnection = () => {
    SendIPCMessage("host_connect", {
        sTCPIp: "127.0.0.1:10400",
    });
}

const Open8814Connection = () => {
    SendIPCMessage("host_connect", {
        sUDPIp: "192.168.4.1"
    });
}

const OpenDeadEndConnection = () => {
    SendIPCMessage("host_connect", {
        sUDPIp: ""
    });
}

const EnableFrameBins = () => {
    SendIPCMessage("app_paths", {
        "/settings/stream/enable_frame_bins": true,
    });
    document.getElementById('enable-frame-bins').hidden = true;
}

const OnHostListMessage = (sReceivedData) => {
    const receivedJson = JSON.parse(sReceivedData);

    ClearHosts();
    for (const server of receivedJson.servers) {
        AddHost(server.name, server.ip);
    }

    if (receivedJson?.servers?.length > 0) {
        HideNoHostsFoundElement();
    } else {
        ShowNoHostsFoundElement();
    }
}

const OnPermissionsStateMessage = (sReceivedData) => {
    const json = JSON.parse(sReceivedData);

    for (const [permission, enabled] of Object.entries(json)) {
        const permissionText = document.querySelector(`.permission-text[data-permission='${permission}']`);
        permissionText.innerText = enabled ? "Allowed" : "Not Allowed!";

        const permissionSection = document.querySelector(`.permission-section[data-permission='${permission}']`);
        const currentPermissionPromptButton = permissionSection.querySelector('.permission-button');
        if (currentPermissionPromptButton) {
            currentPermissionPromptButton.parentNode.removeChild(currentPermissionPromptButton);
        }

        if (!enabled) {
            const promptButtonTemplate = document.getElementById("prompt-permission-button-template");
            permissionSection.querySelector('.permission-button')?.parentNode?.removeChild()

            const promptButtonElement = promptButtonTemplate.content.cloneNode(true);
            promptButtonElement.querySelector('.permission-button').onclick = (e) => {
                SendIPCMessage("request_permission", {
                    sPermission: permission,
                })
            };

            permissionSection.appendChild(promptButtonElement);
        }
    }
}

const ClearSavedPaths = () => {
    SendIPCMessage("clear_saved_paths", {});
}

const DisableShutdownOnExit = () => {
    UpdateAppPath("/settings/stream/disable_exit_on_shutdown", true);
}

const OnMessageReceived = (sMailboxName, sReceivedData) => {
    switch (sMailboxName) {
        case "hosts_list": {
            OnHostListMessage(sReceivedData);
            break;
        }
        case "laser_intersection": {
            OnLaserIntersectionMessage(sReceivedData);
            break;
        }
        case "permissions_state": {
            OnPermissionsStateMessage(sReceivedData);
        }
    }
}

const OnConnect = () => {
}

!(() => {
    SetOnConnectCallback(OnConnect);
    SetIPCMessageCallback(OnMessageReceived);
})();

const NavigateToPage = (sPage) => {
    document.getElementById(sCurrentPage).hidden = true;
    document.getElementById(sPage).hidden = false;

    sCurrentPage = sPage;
}

const RequestAppQuit = () => {
    SendIPCMessage("request_app_quit", {});
}