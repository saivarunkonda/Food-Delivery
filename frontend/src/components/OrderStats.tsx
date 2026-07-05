import React from 'react';
import { Order, OrderStatus } from '../types';

interface OrderStatsProps {
  orders: Order[];
}

const OrderStats: React.FC<OrderStatsProps> = ({ orders }) => {
  const stats = {
    total: orders.length,
    pending: orders.filter(o => o.status === OrderStatus.PENDING).length,
    preparing: orders.filter(o => o.status === OrderStatus.PREPARING).length,
    ready: orders.filter(o => o.status === OrderStatus.READY).length,
    delivered: orders.filter(o => o.status === OrderStatus.DELIVERED).length,
    totalRevenue: orders.reduce((sum, order) => sum + order.totalAmount, 0)
  };

  return (
    <div className="order-stats">
      <div className="stat-card">
        <h3>Total Orders</h3>
        <p className="stat-value">{stats.total}</p>
      </div>
      <div className="stat-card">
        <h3>Pending</h3>
        <p className="stat-value pending">{stats.pending}</p>
      </div>
      <div className="stat-card">
        <h3>Preparing</h3>
        <p className="stat-value preparing">{stats.preparing}</p>
      </div>
      <div className="stat-card">
        <h3>Ready</h3>
        <p className="stat-value ready">{stats.ready}</p>
      </div>
      <div className="stat-card">
        <h3>Delivered</h3>
        <p className="stat-value delivered">{stats.delivered}</p>
      </div>
      <div className="stat-card">
        <h3>Revenue</h3>
        <p className="stat-value revenue">${stats.totalRevenue.toFixed(2)}</p>
      </div>
    </div>
  );
};

export default OrderStats;
