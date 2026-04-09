document.addEventListener('DOMContentLoaded', () => {

    const API_BASE = window.location.hostname.includes('localhost') || window.location.hostname.includes('127.0.0.1') 
                   ? 'http://localhost:8080' 
                   : 'https://anti-gravity-bot.onrender.com';

    const saveBtn = document.getElementById('saveAlertsBtn');
    const testBtn = document.getElementById('testAlertsBtn');
    const killBtn = document.getElementById('killSwitchBtn');
    const toast = document.getElementById('toast');
    
    const ntfyInput = document.getElementById('ntfyTopic');
    const browserToggle = document.getElementById('browserPushToggle');

    let killSwitchActive = false;

    // Load saved settings
    if (localStorage.getItem('ntfyTopic')) {
        ntfyInput.value = localStorage.getItem('ntfyTopic');
    }

    function showToast(msg, isError = false) {
        toast.textContent = msg;
        toast.style.background = isError ? 'var(--red)' : 'var(--green)';
        toast.classList.add('show');
        setTimeout(() => toast.classList.remove('show'), 3000);
    }

    saveBtn.addEventListener('click', async () => {
        const topic = ntfyInput.value.trim();
        localStorage.setItem('ntfyTopic', topic);

        try {
            await fetch(`${API_BASE}/api/alerts/configure`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ ntfyTopic: topic })
            });
            showToast("Preferences saved to backend.");
        } catch (e) {
            showToast("Error connecting to backend.", true);
        }

        // Request browser permission if toggled
        if (browserToggle.checked && Notification.permission !== "granted") {
            Notification.requestPermission();
        }
    });

    testBtn.addEventListener('click', async () => {
        const topic = ntfyInput.value.trim();
        
        try {
            await fetch(`${API_BASE}/api/alerts/test`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ ntfyTopic: topic })
            });
            showToast("Test request sent!");
        } catch (e) {
            showToast("Error sending test.", true);
        }
    });

    killBtn.addEventListener('click', () => {
        killSwitchActive = !killSwitchActive;
        if (killSwitchActive) {
            killBtn.style.background = 'var(--red)';
            killBtn.style.color = 'white';
            killBtn.innerHTML = 'STOPPED (RESUME ALERTS)';
            showToast("ALL ALERTS SUSPENDED", true);
        } else {
            killBtn.style.background = 'rgba(239, 68, 68, 0.1)';
            killBtn.style.color = 'var(--red)';
            killBtn.innerHTML = 'MANUAL OVERRIDE STOP<small>Suppress all alerts</small>';
            showToast("ALERTS RESUMED");
        }
    });

    // Server-Sent Events for Browser Push
    const sse = new EventSource(`${API_BASE}/sse/alerts`);
    
    sse.addEventListener("push-notification", (e) => {
        if (killSwitchActive || !browserToggle.checked) return;

        try {
            const data = JSON.parse(e.data);
            if (Notification.permission === "granted") {
                new Notification(data.title, {
                    body: data.message,
                    icon: 'data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><text y=".9em" font-size="90">⚡</text></svg>'
                });
            } else {
                // Fallback custom alert if native blocked
                alert(`${data.title}\n\n${data.message}`);
            }
        } catch (err) {
            console.error("Error parsing SSE data", err);
        }
    });

    sse.onerror = () => {
        console.error("SSE Connection lost. Backend may be restarting.");
    };
});
