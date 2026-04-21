<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>DropBox File Storage - Demo UI</title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <style>
        body { font-family: Arial, sans-serif; margin: 24px; }
        .card { border: 1px solid #ddd; border-radius: 8px; padding: 16px; margin-bottom: 16px; }
        input, button { padding: 8px; margin: 4px 0; }
        input { width: 320px; max-width: 100%; }
        pre { background: #f7f7f7; padding: 12px; overflow: auto; }
    </style>
</head>
<body>
<h1>DropBox File Storage - Basic JSP UI</h1>

<div class="card">
    <h3>Upload Init</h3>
    <input id="initUserId" placeholder="userId" value="user-1"/><br/>
    <input id="initFileName" placeholder="fileName" value="demo.txt"/><br/>
    <input id="initSize" type="number" placeholder="size" value="1024"/><br/>
    <input id="initChunk" type="number" placeholder="chunkSizeBytes" value="256"/><br/>
    <button onclick="uploadInit()">Call /api/upload/init</button>
</div>

<div class="card">
    <h3>Upload Complete</h3>
    <input id="completeSessionId" placeholder="sessionId"/><br/>
    <input id="completeChecksum" placeholder="checksum" value="abc123"/><br/>
    <input id="completePath" placeholder="path" value="user-1/root/demo.txt"/><br/>
    <input id="completeEtags" placeholder="etags comma separated" value="e1,e2,e3,e4"/><br/>
    <button onclick="uploadComplete()">Call /api/upload/complete</button>
</div>

<div class="card">
    <h3>Get Download URL</h3>
    <input id="downloadFileId" placeholder="fileId"/><br/>
    <button onclick="downloadUrl()">Call /api/files/{fileId}/download</button>
</div>

<div class="card">
    <h3>Create Share Link</h3>
    <input id="shareFileId" placeholder="fileId"/><br/>
    <input id="shareOwnerId" placeholder="ownerId" value="user-1"/><br/>
    <input id="sharePermission" placeholder="permission" value="read"/><br/>
    <input id="shareExpiry" type="number" placeholder="expirySeconds" value="3600"/><br/>
    <button onclick="createShare()">Call /api/shares</button>
</div>

<h3>Response</h3>
<pre id="output">No response yet.</pre>

<script>
    const out = document.getElementById('output');
    function print(data) {
        out.textContent = typeof data === 'string' ? data : JSON.stringify(data, null, 2);
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
