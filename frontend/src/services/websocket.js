import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

class WebSocketService {
  constructor() {
    this.client = null;
    this.isConnected = false;
    this.reconnectAttempts = 0;
    this.maxReconnectAttempts = 10;
  }

  connect(onMessageReceived) {
    if (this.isConnected) {
      console.log('WebSocket already connected');
      return;
    }

    // SockJS expects http:// or https://, NOT wss://
    const isProduction = process.env.NODE_ENV === 'production';
    const wsUrl = isProduction 
      ? 'https://smart-clinic-queue-production.up.railway.app/ws' 
      : 'http://localhost:8080/ws';
    
    console.log('🔗 Connecting to WebSocket:', wsUrl);
    
    this.client = new Client({
      webSocketFactory: () => new SockJS(wsUrl),
      debug: (str) => {
        console.log('[WebSocket Debug]', str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
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
        this.handleReconnect();
      },
      onWebSocketError: (error) => {
        console.error('[WebSocket] WebSocket error:', error);
        this.isConnected = false;
      }
    });

    this.client.activate();
  }

  handleReconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      console.log(`[WebSocket] Reconnect attempt ${this.reconnectAttempts} of ${this.maxReconnectAttempts}`);
      setTimeout(() => {
        if (!this.isConnected) {
          this.client.activate();
        }
      }, 5000 * this.reconnectAttempts);
    } else {
      console.error('[WebSocket] Max reconnect attempts reached');
    }
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.isConnected = false;
      console.log('[WebSocket] Disconnected manually');
    }
  }

  isConnectedStatus() {
    return this.isConnected;
  }
}

const webSocketService = new WebSocketService();
export default webSocketService;