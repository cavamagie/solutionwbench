# -*- coding: utf-8 -*-
from flask import Flask, jsonify, request
import datetime
import os

app = Flask(__name__)

@app.route('/')
def home():
    return '''
    <html>
        <head>
            <title>Python Vulnerable App</title>
            <style>
                body { font-family: Arial; margin: 40px; background: #f5f5f5; }
                .container { background: white; padding: 30px; border-radius: 8px; }
                h1 { color: #d32f2f; }
                .vuln { background: #fff3cd; padding: 10px; margin: 10px 0; border-left: 4px solid #ffc107; }
            </style>
        </head>
        <body>
            <div class="container">
                <h1>🐍 Python Vulnerable Test Application</h1>
                <div class="vuln">
                    <strong>⚠️ Warning:</strong> This application contains intentionally outdated Python dependencies
                </div>
                <h2>Vulnerable Dependencies</h2>
                <ul>
                    <li>Flask 0.12.2 (multiple CVEs)</li>
                    <li>Django 1.11.0 (SQL injection, XSS)</li>
                    <li>requests 2.6.0 (SSL verification issues)</li>
                    <li>Jinja2 2.9.6 (SSTI vulnerabilities)</li>
                    <li>SQLAlchemy 1.1.0 (SQL injection)</li>
                    <li>Werkzeug 0.11.0 (security issues)</li>
                    <li>PyYAML 3.12 (arbitrary code execution)</li>
                    <li>cryptography 1.7.0 (weak crypto)</li>
                    <li>Pillow 4.0.0 (image processing vulns)</li>
                </ul>
                <h2>Endpoints</h2>
                <p><strong>GET /api/info</strong> - Application info</p>
                <p><strong>GET /api/health</strong> - Health check</p>
            </div>
        </body>
    </html>
    '''

@app.route('/api/info')
def info():
    return jsonify({
        'app': 'Python Vulnerable Test App',
        'language': 'Python',
        'framework': 'Flask 0.12.2',
        'timestamp': str(datetime.datetime.now()),
        'vulnerabilities': 'intentional'
    })

@app.route('/api/health')
def health():
    return jsonify({
        'status': 'running',
        'timestamp': str(datetime.datetime.now())
    })

if __name__ == '__main__':
    port = int(os.environ.get('PORT', 8080))
    app.run(host='0.0.0.0', port=port)

# Made with Bob
