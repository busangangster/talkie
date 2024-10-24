import { WebSocketServer } from 'ws';
import { RealtimeClient } from '@openai/realtime-api-beta';

class RealtimeRelay {
  constructor(apiKey) {
    this.apiKey = apiKey;
    this.wss = null;
  }

  listen(port) {
    this.wss = new WebSocketServer({ port });
    this.wss.on('connection', this.connectionHandler.bind(this));
    console.log(`Listening on ws://localhost:${port}`);
  }

  async connectionHandler(ws, req) {
    if (!req.url) {
      console.log('No URL provided, closing connection.');
      ws.close();
      return;
    }

    const url = new URL(req.url, `http://${req.headers.host}`);
    const pathname = url.pathname;

    if (pathname !== '/') {
      console.log(`Invalid pathname: "${pathname}"`);
      ws.close();
      return;
    }

    // Instantiate new client
    console.log(`Connecting with key "${this.apiKey.slice(0, 3)}..."`);
    const client = new RealtimeClient({ apiKey: this.apiKey });

    // Relay: OpenAI Realtime API Event -> Browser Event
    client.realtime.on('server.*', (event) => {
      console.log(`Relaying "${event.type}" to Client`);
      ws.send(JSON.stringify(event));
    });
    client.realtime.on('close', () => ws.close());

    // Relay: Browser Event -> OpenAI Realtime API Event
    const messageQueue = [];
    const messageHandler = (data) => {
      try {
        const event = JSON.parse(data);
        console.log(`Relaying "${event.type}" to OpenAI`);

        client.realtime.send(event.type, event);
      } catch (e) {
        console.error(e.message);
        console.log(`Error parsing event from client: ${data}`);
      }
    };

    ws.on('message', (data) => {
      if (!client.isConnected()) {
        messageQueue.push(data);
      } else {
        messageHandler(data);
      }
    });

    ws.on('close', () => client.disconnect());

    // Connect to OpenAI Realtime API
    try {
      console.log(`Connecting to OpenAI...`);
      await client.connect();
    } catch (e) {
      console.log(`Error connecting to OpenAI: ${e.message}`);
      ws.close();
      return;
    }
    console.log(`Connected to OpenAI successfully!`);

    while (messageQueue.length) {
      messageHandler(messageQueue.shift());
    }
  }
}

// 서버 시작
const apiKey = 'YOUR_OPENAI_API_KEY'; // OpenAI API 키 입력
const relay = new RealtimeRelay(apiKey);
relay.listen(8081); // 웹소켓 서버가 8081 포트에서 리슨하도록 설정
