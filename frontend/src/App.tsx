import React, { useState, useEffect } from 'react';
import OrderForm from './components/OrderForm';
import OrderList from './components/OrderList';
import OrderStats from './components/OrderStats';
import { Order } from './types';
import { orderService } from './services/orderService';
import { webSocketService } from './services/webSocketService';
import './App.css';

function App() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    // Load initial orders
    loadOrders();

    // Connect to WebSocket
    webSocketService.connect();
    setConnected(true);

    // Listen for real-time updates
    webSocketService.onMessage((message) => {
      const data = JSON.parse(message);
      handleRealTimeUpdate(data);
    });

    return () => {
      webSocketService.disconnect();
    };
  }, []);

  const loadOrders = async () => {
    try {
      const data = await orderService.getPendingOrders();
      setOrders(data);
    } catch (error) {
      console.error('Error loading orders:', error);
    }
  };

  const handleRealTimeUpdate = (data: any) => {
    if (data.type === 'ORDER_CREATED' || data.type === 'ORDER_STATUS_UPDATE') {
      loadOrders();
    }
  };

  const handleOrderCreated = (order: Order) => {
    setOrders([order, ...orders]);
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>🍔 Food Delivery Order Management</h1>
        <div className="connection-status">
          <span className={`status-indicator ${connected ? 'connected' : 'disconnected'}`}>
            {connected ? '● Connected' : '○ Disconnected'}
          </span>
        </div>
      </header>

      <main className="App-main">
        <div className="stats-section">
          <OrderStats orders={orders} />
        </div>

        <div className="content-section">
          <div className="form-section">
            <OrderForm onOrderCreated={handleOrderCreated} />
          </div>

          <div className="orders-section">
            <OrderList orders={orders} onOrderUpdate={loadOrders} />
          </div>
        </div>
      </main>
    </div>
  );
}

export default App;
