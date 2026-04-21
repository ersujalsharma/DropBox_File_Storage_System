<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>DropBox File Storage - Demo UI</title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <style>
        :root {
            --bg: #0f172a;
            --panel: #111827;
            --panel-soft: #1f2937;
            --text: #e5e7eb;
            --muted: #9ca3af;
            --primary: #22d3ee;
            --primary-2: #3b82f6;
            --success: #22c55e;
            --border: #374151;
        }

        * { box-sizing: border-box; }

        body {
            margin: 0;
            font-family: Inter, Segoe UI, Arial, sans-serif;
            color: var(--text);
            background: radial-gradient(1200px 600px at 10% -20%, #1d4ed8 0%, rgba(29,78,216,0) 60%),
                        radial-gradient(1000px 500px at 110% 0%, #0891b2 0%, rgba(8,145,178,0) 50%),
                        var(--bg);
        }

        .container {
            max-width: 1150px;
            margin: 0 auto;
            padding: 32px 20px 56px;
        }

        .hero {
            background: linear-gradient(135deg, rgba(34,211,238,.16), rgba(59,130,246,.16));
            border: 1px solid rgba(148,163,184,.22);
            border-radius: 16px;
            padding: 24px;
            margin-bottom: 18px;
            backdrop-filter: blur(4px);
        }

        .hero h1 { margin: 0 0 8px; font-size: 28px; }
        .hero p { margin: 0; color: var(--muted); }

        .grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
            gap: 14px;
        }

        .card {
            background: linear-gradient(180deg, rgba(31,41,55,.95), rgba(17,24,39,.95));
            border: 1px solid var(--border);
            border-radius: 14px;
            padding: 16px;
            box-shadow: 0 8px 24px rgba(0,0,0,.25);
        }

        .card.wide { grid-column: 1 / -1; }

        .card h3 {
            margin: 0 0 12px;
            font-size: 16px;
        }

        label {
            display: block;
            margin-bottom: 4px;
            color: var(--muted);
            font-size: 12px;
        }

        input {
            width: 100%;
            background: var(--panel-soft);
            border: 1px solid var(--border);
            color: var(--text);
            border-radius: 10px;
            padding: 10px 12px;
            margin-bottom: 8px;
            outline: none;
        }

        input[type="file"] { padding: 9px; }

        input:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(34,211,238,.15);
        }

        button {
            width: 100%;
            border: none;
            border-radius: 10px;
            color: #0b1220;
            font-weight: 700;
            padding: 10px 12px;
            cursor: pointer;
            background: linear-gradient(90deg, var(--primary), var(--primary-2));
        }

        .btn-secondary {
            margin-top: 8px;
            background: linear-gradient(90deg, #a78bfa, #22d3ee);
        }

        button:hover { filter: brightness(1.05); }

        .hint {
            margin-top: 8px;
            color: var(--muted);
            font-size: 12px;
            line-height: 1.45;
        }

        .progress-wrap {
            width: 100%;
            background: #0b1220;
            border: 1px solid var(--border);
            border-radius: 999px;
            overflow: hidden;
            margin-top: 8px;
        }

        .progress {
            width: 0;
            height: 10px;
            background: linear-gradient(90deg, var(--success), #4ade80);
            transition: width .2s ease;
        }

        .response {
            margin-top: 14px;
            background: #020617;
            border: 1px solid #1e293b;
            border-radius: 14px;
            overflow: hidden;
        }

        .response-head {
            padding: 10px 14px;
            background: rgba(15,23,42,.8);
            border-bottom: 1px solid #1e293b;
            display: flex;
            align-items: center;
            justify-content: space-between;
            color: var(--muted);
            font-size: 13px;
        }

        .badge {
            padding: 3px 8px;
            border-radius: 999px;
            background: rgba(34,197,94,.18);
            border: 1px solid rgba(34,197,94,.35);
            color: #86efac;
            font-size: 11px;
            font-weight: 700;
        }

        pre {
            margin: 0;
            max-height: 280px;
            overflow: auto;
            padding: 14px;
            color: #d1fae5;
            font-size: 12px;
        }
    </style>
</head>
<body>
<div class="container">
    <section class="hero">
        <h1>DropBox File Storage • Demo Console</h1>
        <p>Upload a real file from browser, split into chunks, PUT each chunk to pre-signed URLs, and then complete upload.</p>
    </section>

    <section class="grid">
        <div class="card wide">
            <h3>🚀 One-Click File Upload (Chunked)</h3>
            <label>User ID</label>
            <input id="autoUserId" placeholder="userId" value="user-1"/>
            <label>Select File</label>
            <input id="autoFile" type="file"/>
            <label>Chunk Size (bytes)</label>
            <input id="autoChunkSize" type="number" value="5242880"/>
            <label>Path for Metadata (S3 object key)</label>
            <input id="autoPath" placeholder="user-1/root/my-file.bin" value="user-1/root/demo.txt"/>
            <button onclick="uploadFileEndToEnd()">Upload File End-to-End</button>
            <div class="progress-wrap"><div id="uploadProgress" class="progress"></div></div>
            <div class="hint">Flow: init → split file → PUT chunks to signed URLs → complete upload. Ensure S3 bucket CORS allows PUT from your frontend origin.</div>
        </div>

        <div class="card">
            <h3>1) Upload Init (Manual)</h3>
            <label>User ID</label>
            <input id="initUserId" placeholder="userId" value="user-1"/>
            <label>File Name</label>
            <input id="initFileName" placeholder="fileName" value="demo.txt"/>
            <label>File Size (bytes)</label>
            <input id="initSize" type="number" placeholder="size" value="1024"/>
            <label>Chunk Size (bytes)</label>
            <input id="initChunk" type="number" placeholder="chunkSizeBytes" value="256"/>
            <button onclick="uploadInit()">POST /api/upload/init</button>
        </div>

        <div class="card">
            <h3>2) Upload Complete (Manual)</h3>
            <label>Session ID</label>
            <input id="completeSessionId" placeholder="sessionId"/>
            <label>Checksum</label>
            <input id="completeChecksum" placeholder="checksum" value="abc123"/>
            <label>File Path</label>
            <input id="completePath" placeholder="path" value="user-1/root/demo.txt"/>
            <label>ETags (comma-separated)</label>
            <input id="completeEtags" placeholder="e1,e2,e3,e4" value="e1,e2,e3,e4"/>
            <button onclick="uploadComplete()">POST /api/upload/complete</button>
        </div>

        <div class="card">
            <h3>3) Download URL</h3>
            <label>File ID</label>
            <input id="downloadFileId" placeholder="fileId"/>
            <button onclick="downloadUrl()">GET /api/files/{fileId}/download</button>
        </div>

        <div class="card">
            <h3>4) Share Link</h3>
            <label>File ID</label>
            <input id="shareFileId" placeholder="fileId"/>
            <label>Owner ID</label>
            <input id="shareOwnerId" placeholder="ownerId" value="user-1"/>
            <label>Permission</label>
            <input id="sharePermission" placeholder="permission" value="read"/>
            <label>Expiry (seconds)</label>
            <input id="shareExpiry" type="number" placeholder="expirySeconds" value="3600"/>
            <button onclick="createShare()">POST /api/shares</button>
        </div>
    </section>

    <section class="response">
        <div class="response-head">
            <span>API Response</span>
            <span class="badge">LIVE</span>
        </div>
        <pre id="output">No response yet.</pre>
    </section>
</div>

<script>
    const out = document.getElementById('output');
    const progressEl = document.getElementById('uploadProgress');

    function print(data) {
        out.textContent = typeof data === 'string' ? data : JSON.stringify(data, null, 2);
    }

    function setProgress(percent) {
        progressEl.style.width = Math.max(0, Math.min(100, percent)) + '%';
    }

    async function request(url, options = {}) {
        const res = await fetch(url, {
            headers: {'Content-Type': 'application/json'},
            ...options
        });
        const json = await res.json();
        if (!res.ok) throw new Error(JSON.stringify(json));
        return json;
    }

    async function uploadFileEndToEnd() {
        try {
            setProgress(0);
            const fileInput = document.getElementById('autoFile');
            const file = fileInput.files[0];
            if (!file) throw new Error('Please select a file first.');

            const userId = document.getElementById('autoUserId').value;
            const chunkSizeBytes = Number(document.getElementById('autoChunkSize').value);
            const path = document.getElementById('autoPath').value || (`${userId}/root/${file.name}`);

            const initBody = {
                userId,
                fileName: file.name,
                size: file.size,
                chunkSizeBytes
            };

            const initData = await request('/api/upload/init', {
                method: 'POST',
                body: JSON.stringify(initBody)
            });

            const etags = [];
            for (let i = 0; i < initData.chunkUrls.length; i++) {
                const start = i * chunkSizeBytes;
                const end = Math.min(start + chunkSizeBytes, file.size);
                const chunk = file.slice(start, end);

                const putRes = await fetch(initData.chunkUrls[i], {
                    method: 'PUT',
                    body: chunk
                });

                if (!putRes.ok) {
                    throw new Error(`Chunk upload failed at index ${i}: ${putRes.status}`);
                }

                const etag = putRes.headers.get('ETag') || `etag-${i}`;
                etags.push(etag.replaceAll('"', ''));
                setProgress(((i + 1) / initData.chunkUrls.length) * 90);
            }

            const completeBody = {
                sessionId: initData.sessionId,
                checksum: `size-${file.size}`,
                etags,
                path
            };

            const completeData = await request('/api/upload/complete', {
                method: 'POST',
                body: JSON.stringify(completeBody)
            });

            document.getElementById('completeSessionId').value = initData.sessionId;
            document.getElementById('completePath').value = path;
            document.getElementById('completeEtags').value = etags.join(',');
            document.getElementById('downloadFileId').value = completeData.fileId;
            document.getElementById('shareFileId').value = completeData.fileId;
            setProgress(100);

            print({
                message: 'Upload complete',
                init: initData,
                complete: completeData,
                uploadedChunks: etags.length
            });
        } catch (e) {
            print(e.message);
        }
    }

    async function uploadInit() {
        try {
            const body = {
                userId: document.getElementById('initUserId').value,
                fileName: document.getElementById('initFileName').value,
                size: Number(document.getElementById('initSize').value),
                chunkSizeBytes: Number(document.getElementById('initChunk').value)
            };
            const data = await request('/api/upload/init', {method: 'POST', body: JSON.stringify(body)});
            document.getElementById('completeSessionId').value = data.sessionId;
            print(data);
        } catch (e) {
            print(e.message);
        }
    }

    async function uploadComplete() {
        try {
            const etags = document.getElementById('completeEtags').value.split(',').map(v => v.trim()).filter(Boolean);
            const body = {
                sessionId: document.getElementById('completeSessionId').value,
                checksum: document.getElementById('completeChecksum').value,
                etags,
                path: document.getElementById('completePath').value
            };
            const data = await request('/api/upload/complete', {method: 'POST', body: JSON.stringify(body)});
            document.getElementById('downloadFileId').value = data.fileId;
            document.getElementById('shareFileId').value = data.fileId;
            print(data);
        } catch (e) {
            print(e.message);
        }
    }

    async function downloadUrl() {
        try {
            const fileId = document.getElementById('downloadFileId').value;
            const data = await request('/api/files/' + encodeURIComponent(fileId) + '/download');
            print(data);
        } catch (e) {
            print(e.message);
        }
    }

    async function createShare() {
        try {
            const body = {
                fileId: document.getElementById('shareFileId').value,
                ownerId: document.getElementById('shareOwnerId').value,
                permission: document.getElementById('sharePermission').value,
                expirySeconds: Number(document.getElementById('shareExpiry').value)
            };
            const data = await request('/api/shares', {method: 'POST', body: JSON.stringify(body)});
            print(data);
        } catch (e) {
            print(e.message);
        }
    }
</script>
</body>
</html>
