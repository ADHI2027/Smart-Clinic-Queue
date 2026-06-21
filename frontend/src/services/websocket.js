import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

class WebSocketService {
  constructor() {
    this.client = null;
    this.isConnected = false;
    this.reconnectAttempts = 0;
    this.maxReconnectAttempts = 3;
  }

  connect(onMessageReceived) {
    if (this.isConnected) {
      console.log('WebSocket already connected');
      return;
    }

    const wsUrl = 'http://localhost:8080/ws';
    
    this.client = new Client({
      webSocketFactory: () => new SockJS(wsUrl),
      debug: (str) => {
        console.log('[WebSocket]', str);
      },
      reconnectDelay: 5000,
      onConnect: () => {
        console.log('[WebSocket] ✅ Connected successfully!');
        this.isConnected = true;
        this.reconnectAttempts = 0;
        
        this.client.subscribe('/topic/queue', (message) => {
          try {
            const queueData = JSON.parse(message.body);
            console.log('[WebSocket] 📡 Queue update received:', queueData);
            if (onMessageReceived) {
              onMessageReceived(queueData);
            }
          } catch (error) {
            console.error('[WebSocket] Error parsing message:', error);
          }
        });
      },
      onStompError: (frame) => {
        console.error('[WebSocket] STOMP error:', frame);
      },
      onWebSocketClose: () => {
        console.log('[WebSocket] Connection closed');
        this.isConnected = false;
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
          this.reconnectAttempts++;
          console.log(`[WebSocket] Reconnecting (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);
          setTimeout(() => {
            this.client.activate();
          }, 5000);
        } else {
          console.log('[WebSocket] Max reconnect attempts reached');
        }
      },
      onWebSocketError: (error) => {
        console.error('[WebSocket] Error:', error);
        this.isConnected = false;
      }
    });

    this.client.activate();
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.isConnected = false;
      console.log('[WebSocket] Disconnected');
    }
  }

  isConnectedStatus() {
    return this.isConnected;
  }
}

const webSocketService = new WebSocketService();
export default webSocketService;