document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('loginForm');
    const submitBtn = document.getElementById('submitBtn');
    const errorMsg = document.getElementById('errorMsg');
    const timerDisplay = document.getElementById('totpTimer');

    // API URL handling (Local vs Production)
    const API_BASE = window.location.hostname.includes('localhost') || window.location.hostname.includes('127.0.0.1') 
                   ? 'http://localhost:8080' 
                   : 'https://anti-gravity-trading-bot-1.onrender.com';

    // Simple visual countdown for TOTP to remind user it rotates
    let secondsLeft = 30;
    setInterval(() => {
        secondsLeft--;
        if (secondsLeft <= 0) secondsLeft = 30;
        timerDisplay.textContent = `⏱ ${secondsLeft}s`;
        if (secondsLeft <= 5) {
            timerDisplay.style.color = '#ef4444';
        } else {
            timerDisplay.style.color = '#38bdf8';
        }
    }, 1000);

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const clientId = document.getElementById('clientId').value;
        const password = document.getElementById('password').value;
        const apiKey = document.getElementById('apiKey').value;
        const totp = document.getElementById('totp').value;

        submitBtn.disabled = true;
        submitBtn.textContent = 'CONNECTING...';
        errorMsg.style.display = 'none';

        try {
            const response = await fetch(`${API_BASE}/api/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ clientId, password, apiKey, totp })
            });

            const data = await response.json();

            if (response.ok && data.success) {
                // Success! Redirect to dashboard
                submitBtn.style.background = '#10b981';
                submitBtn.textContent = 'SUCCESS! REDIRECTING...';
                
                setTimeout(() => {
                    window.location.href = 'dashboard.html';
                }, 800);
            } else {
                // Auth failed
                errorMsg.textContent = data.message || 'Authentication failed.';
                errorMsg.style.display = 'block';
                submitBtn.disabled = false;
                submitBtn.textContent = 'LAUNCH DASHBOARD →';
            }
        } catch (err) {
            errorMsg.textContent = 'Server unreachable. Ensure backend is running.';
            errorMsg.style.display = 'block';
            submitBtn.disabled = false;
            submitBtn.textContent = 'LAUNCH DASHBOARD →';
        }
    });
});
