import React from 'react';
import { Order, OrderStatus } from '../types';
import { orderService } from '../services/orderService';

interface OrderListProps {
  orders: Order[];
  onOrderUpdate: () => void;
}

const OrderList: React.FC<OrderListProps> = ({ orders, onOrderUpdate }) => {
  const handleStatusChange = async (orderId: string, newStatus: string) => {
    try {
      await orderService.updateOrderStatus(orderId, newStatus);
      onOrderUpdate();
    } catch (error) {
      console.error('Error updating order status:', error);
      alert('Failed to update order status');
    }
  };

  const getStatusColor = (status: OrderStatus) => {
    switch (status) {
      case OrderStatus.PENDING:
        return '#ffc107';
      case OrderStatus.ACCEPTED:
        return '#17a2b8';
      case OrderStatus.PREPARING:
        return '#007bff';
      case OrderStatus.READY:
        return '#28a745';
      case OrderStatus.PICKED_UP:
        return '#6610f2';
      case OrderStatus.DELIVERED:
        return '#28a745';
      case OrderStatus.CANCELLED:
        return '#dc3545';
      default:
        return '#6c757d';
    }
  };

  return (
    <div className="order-list">
      <h2>Orders ({orders.length})</h2>
      {orders.length === 0 ? (
        <p className="no-orders">No orders available</p>
      ) : (
        <div className="orders-grid">
          {orders.map((order) => (
            <div key={order.id} className="order-card">
              <div className="order-header">
                <h3>Order #{order.id.slice(0, 8)}</h3>
                <span 
                  className="status-badge" 
                  style={{ backgroundColor: getStatusColor(order.status) }}
                >
                  {order.status}
                </span>
              </div>

              <div className="order-details">
                <p><strong>Customer:</strong> {order.customerPhone}</p>
                <p><strong>Address:</strong> {order.deliveryAddress}</p>
                <p><strong>Total:</strong> ${order.totalAmount.toFixed(2)}</p>
                <p><strong>Created:</strong> {new Date(order.createdAt).toLocaleString()}</p>
              </div>

              <div className="order-items">
                <h4>Items:</h4>
                {order.items.map((item, index) => (
                  <div key={index} className="order-item">
                    {item.itemName} x {item.quantity} - ${item.price.toFixed(2)}
                  </div>
                ))}
              </div>

              <div className="order-actions">
                <select
                  value={order.status}
                  onChange={(e) => handleStatusChange(order.id, e.target.value)}
                  className="status-select"
                >
                  <option value={OrderStatus.PENDING}>Pending</option>
                  <option value={OrderStatus.ACCEPTED}>Accepted</option>
                  <option value={OrderStatus.PREPARING}>Preparing</option>
                  <option value={OrderStatus.READY}>Ready</option>
                  <option value={OrderStatus.PICKED_UP}>Picked Up</option>
                  <option value={OrderStatus.DELIVERED}>Delivered</option>
                  <option value={OrderStatus.CANCELLED}>Cancelled</option>
                </select>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default OrderList;
