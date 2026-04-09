document.addEventListener('DOMContentLoaded', () => {

    const API_BASE = window.location.hostname.includes('localhost') || window.location.hostname.includes('127.0.0.1') 
                   ? 'http://localhost:8080' 
                   : 'https://anti-gravity-trading-bot-1.onrender.com';

    let currentSymbol = 'NIFTY';
    let currentExpiry = '';

    // Elements
    const btnNifty = document.getElementById('btnNifty');
    const btnBankNifty = document.getElementById('btnBankNifty');
    const expirySelect = document.getElementById('expirySelect');
    const liveClock = document.getElementById('liveClock');
    const sessionText = document.getElementById('sessionText');
    const vixBadge = document.getElementById('vixBadge');
    const spotPriceDisplay = document.getElementById('spotPriceDisplay');
    const pcrDisplay = document.getElementById('pcrDisplay');
    const maxPainDisplay = document.getElementById('maxPainDisplay');
    const signalsContainer = document.getElementById('signalsContainer');
    const chainBody = document.getElementById('chainBody');

    // Live IST Clock
    setInterval(() => {
        const d = new Date();
        const istTime = d.toLocaleTimeString('en-US', { timeZone: 'Asia/Kolkata', hour12: false });
        liveClock.textContent = istTime + ' IST';
    }, 1000);

    // Toggle Symbol
    btnNifty.addEventListener('click', () => {
        currentSymbol = 'NIFTY';
        btnNifty.classList.add('active');
        btnBankNifty.classList.remove('active');
        currentExpiry = ''; // Reset on change
        fetchData();
    });

    btnBankNifty.addEventListener('click', () => {
        currentSymbol = 'BANKNIFTY';
        btnBankNifty.classList.add('active');
        btnNifty.classList.remove('active');
        currentExpiry = ''; // Reset on change
        fetchData();
    });

    expirySelect.addEventListener('change', (e) => {
        currentExpiry = e.target.value;
        fetchData();
    });

    // Main Fetch Loop
    async function fetchData() {
        try {
            const res = await fetch(`${API_BASE}/api/option-chain?symbol=${currentSymbol}&expiry=${currentExpiry}`);
            const data = await res.json();

            updateDashboard(data);
        } catch (err) {
            console.error("Failed to fetch dashboard data:", err);
        }
    }

    function updateDashboard(data) {
        // Expiry Dropdown
        if (expirySelect.options.length === 0 || currentExpiry === '') {
            expirySelect.innerHTML = '';
            data.availableExpiries.forEach(exp => {
                const opt = document.createElement('option');
                opt.value = opt.textContent = exp;
                if (exp === data.expiryDate) opt.selected = true;
                expirySelect.appendChild(opt);
            });
            currentExpiry = data.expiryDate;
        }

        // Header Metrics
        spotPriceDisplay.textContent = `Spot: ${data.spotPrice.toFixed(2)}`;
        vixBadge.textContent = `VIX: ${data.vix.toFixed(2)} (${data.vixLevel})`;
        
        let sessionColor = data.sessionType === 'TRENDING' ? 'var(--green)' : 'var(--yellow)';
        sessionText.innerHTML = `⚡ SESSION: <span style="color:${sessionColor}">${data.sessionType} DAY</span>`;

        pcrDisplay.textContent = `PCR: ${data.analytics.pcr.toFixed(2)}`;
        maxPainDisplay.textContent = `Max Pain: ${data.analytics.maxPain}`;

        // Build Signals
        signalsContainer.innerHTML = '';
        data.signals.forEach(sig => {
            const div = document.createElement('div');
            div.className = `signal-card ${sig.color}`;
            
            let extra = '';
            if (sig.targetPremium) {
                extra = `<div style="font-size:11px; margin-top:8px; display:flex; gap:10px;">
                            <span style="color:var(--green)">Target: ${sig.targetPremium}</span>
                            <span style="color:var(--red)">SL: ${sig.slPremium}</span>
                         </div>`;
            }

            div.innerHTML = `
                <div class="sc-header">
                    <span>${sig.strategyName}</span>
                    <span>Strike: ${sig.recommendedStrike || '--'}</span>
                </div>
                <div class="sc-action">${sig.action}</div>
                <div class="sc-rationale">${sig.rationale}</div>
                ${extra}
            `;
            signalsContainer.appendChild(div);
        });

        // Build Option Chain
        chainBody.innerHTML = '';
        data.chain.forEach(row => {
            const tr = document.createElement('tr');
            if (row.atm) tr.classList.add('row-atm');

            const makeHl = (val) => val > 1000 ? (val/100000).toFixed(2) + 'L' : val;

            tr.innerHTML = `
                <td class="col-ce">${makeHl(row.ce.oi)}</td>
                <td class="col-ce">${row.ce.iv}%</td>
                <td class="col-ce" style="font-weight:700">${row.ce.ltp.toFixed(2)}</td>
                
                <td class="col-strike">${row.strikePrice}</td>
                
                <td class="col-pe" style="font-weight:700">${row.pe.ltp.toFixed(2)}</td>
                <td class="col-pe">${row.pe.iv}%</td>
                <td class="col-pe">${makeHl(row.pe.oi)}</td>
            `;
            chainBody.appendChild(tr);
        });
    }

    // Polling every 10 seconds
    fetchData();
    setInterval(fetchData, 10000);
});
