import axios from 'axios';
import { Order, OrderRequest } from '../types';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

export const orderService = {
  async createOrder(order: OrderRequest): Promise<Order> {
    const response = await axios.post(`${API_BASE_URL}/api/orders`, order);
    return response.data;
  },

  async getOrderById(id: string): Promise<Order> {
    const response = await axios.get(`${API_BASE_URL}/api/orders/${id}`);
    return response.data;
  },

  async getOrdersByRestaurant(restaurantId: string): Promise<Order[]> {
    const response = await axios.get(`${API_BASE_URL}/api/orders/restaurant/${restaurantId}`);
    return response.data;
  },

  async getOrdersByCustomer(customerId: string): Promise<Order[]> {
    const response = await axios.get(`${API_BASE_URL}/api/orders/customer/${customerId}`);
    return response.data;
  },

  async getPendingOrders(): Promise<Order[]> {
    const response = await axios.get(`${API_BASE_URL}/api/orders/pending`);
    return response.data;
  },

  async updateOrderStatus(id: string, status: string): Promise<Order> {
    const response = await axios.put(`${API_BASE_URL}/api/orders/${id}/status`, null, {
      params: { status }
    });
    return response.data;
  },

  async assignDriver(orderId: string, driverId: string): Promise<Order> {
    const response = await axios.put(`${API_BASE_URL}/api/orders/${orderId}/assign-driver`, null, {
      params: { driverId }
    });
    return response.data;
  }
};
