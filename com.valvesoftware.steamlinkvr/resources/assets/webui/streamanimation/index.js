//document.body.style.opacity = 0;

var fadeOutIntervalID = -1;
var fadeInIntervalID = -1;
var fadeOutDelayID = -1;

function fadeIn(totalTime)
{
    if (fadeOutIntervalID != -1)
    {
        console.log('existing fadeout running. stopping fadeout.');
        clearInterval(fadeOutIntervalID);
        fadeOutIntervalID = -1;
    }
    if (fadeInIntervalID != -1)
    {
        console.log('existing fadein running. exiting.');
        return;
    }

    console.log('fadein running');

    var fade = document.body;
    var opacity = parseFloat(fade.style.opacity);
    fadeInIntervalID = setInterval(function()
    {
        if (opacity < 1)
        {
            opacity = opacity + 0.05
            fade.style.opacity = opacity;
        }
        else
        {
            clearInterval(fadeInIntervalID);
            fadeInIntervalID = -1;
        }
    }, totalTime / 20);
}

function fadeOut(totalTime, delayTime)
{
    if (fadeOutDelayID != -1)
    {
        console.log('existing delayed fadeout running. exiting.');
        clearTimeout(fadeOutDelayID);
        fadeOutDelayID = -1;
    }

    fadeOutDelayID = setTimeout(function()
    {
        if (fadeInIntervalID != -1)
        {
            console.log('existing fadeout running. exiting.');
            clearInterval(fadeInIntervalID);
            fadeInIntervalID = -1;
        }
        if (fadeOutIntervalID != -1)
        {
            console.log('existing fadeout running. exiting.');
            return;
        }

        var fade = document.body;
        var opacity = parseFloat(fade.style.opacity);
        var perTick = 0.05;
        fadeOutIntervalID = setInterval(function()
        {
            if (opacity > 0)
            {
                opacity = opacity - perTick
                fade.style.opacity = opacity;
            }
            else
            {
                clearInterval(fadeOutIntervalID);
                fadeOutIntervalID = -1;
            }
        }, totalTime / (1 / perTick));

        fadeOutDelayID = -1;
    }, delayTime);
}

const OnMessageReceived = (sMailboxName, sReceivedData) => {
    switch (sMailboxName) {
        case "fade_in": {
            //fadeIn(100);
            break;
        }
        case "fade_out": {
            const json = JSON.parse(sReceivedData);
            //fadeOut(500, json?.fDelay);
            break;
        }
    }
}

!(() => {
    SetIPCMessageCallback(OnMessageReceived);
})();