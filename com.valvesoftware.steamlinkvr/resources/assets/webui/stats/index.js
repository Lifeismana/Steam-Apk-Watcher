const ctx = document.getElementById('myChart');

const frameBinChart = new Chart(ctx, {
    type: 'bar',
    data: {
        labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
        datasets: [{
            label: 'Frame bins',
            data: [12, 19, 3, 5, 2, 3],
            borderWidth: 1
        }]
    },
    options: {
        scales: {
            y: {
                beginAtZero: true
            }
        }
    }
});

const OnUpdateStats = (sReceivedData) => {

    const json = JSON.parse(sReceivedData);

    if (Object.keys(json).includes("frame_bins")) {
        let labels = [];
        for (let i = 0; i < json["frame_bins"].length; i++) {
            labels.push(i);
        }
        frameBinChart.data.labels = labels;
        frameBinChart.data.datasets[0].data = json["frame_bins"];

        frameBinChart.update();
    }
}

const OnMessageReceived = (sMailboxName, sReceivedData) => {
    switch (sMailboxName) {
        case "stats": {
            OnUpdateStats(sReceivedData);
            break;
        }
    }
}

!(() => {
    SetIPCMessageCallback(OnMessageReceived);
})();