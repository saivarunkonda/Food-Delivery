import React, { useState } from 'react';
import { OrderRequest, OrderItemRequest } from '../types';
import { orderService } from '../services/orderService';

interface OrderFormProps {
  onOrderCreated: (order: any) => void;
}

const OrderForm: React.FC<OrderFormProps> = ({ onOrderCreated }) => {
  const [order, setOrder] = useState<OrderRequest>({
    customerId: '',
    restaurantId: '',
    totalAmount: 0,
    deliveryAddress: '',
    customerPhone: '',
    items: []
  });

  const [currentItem, setCurrentItem] = useState<OrderItemRequest>({
    itemName: '',
    quantity: 1,
    price: 0,
    specialInstructions: ''
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const createdOrder = await orderService.createOrder(order);
      onOrderCreated(createdOrder);
      
      // Reset form
      setOrder({
        customerId: '',
        restaurantId: '',
        totalAmount: 0,
        deliveryAddress: '',
        customerPhone: '',
        items: []
      });
      setCurrentItem({
        itemName: '',
        quantity: 1,
        price: 0,
        specialInstructions: ''
      });
    } catch (error) {
      console.error('Error creating order:', error);
      alert('Failed to create order');
    }
  };

  const addItem = () => {
    if (currentItem.itemName && currentItem.quantity > 0 && currentItem.price > 0) {
      setOrder({
        ...order,
        items: [...order.items, { ...currentItem }],
        totalAmount: order.totalAmount + (currentItem.quantity * currentItem.price)
      });
      setCurrentItem({
        itemName: '',
        quantity: 1,
        price: 0,
        specialInstructions: ''
      });
    }
  };

  return (
    <div className="order-form">
      <h2>Create New Order</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Customer ID:</label>
          <input
            type="text"
            value={order.customerId}
            onChange={(e) => setOrder({ ...order, customerId: e.target.value })}
            required
          />
        </div>

        <div className="form-group">
          <label>Restaurant ID:</label>
          <input
            type="text"
            value={order.restaurantId}
            onChange={(e) => setOrder({ ...order, restaurantId: e.target.value })}
            required
          />
        </div>

        <div className="form-group">
          <label>Delivery Address:</label>
          <input
            type="text"
            value={order.deliveryAddress}
            onChange={(e) => setOrder({ ...order, deliveryAddress: e.target.value })}
            required
          />
        </div>

        <div className="form-group">
          <label>Customer Phone:</label>
          <input
            type="tel"
            value={order.customerPhone}
            onChange={(e) => setOrder({ ...order, customerPhone: e.target.value })}
            required
          />
        </div>

        <div className="form-section">
          <h3>Add Items</h3>
          <div className="form-group">
            <label>Item Name:</label>
            <input
              type="text"
              value={currentItem.itemName}
              onChange={(e) => setCurrentItem({ ...currentItem, itemName: e.target.value })}
            />
          </div>

          <div className="form-group">
            <label>Quantity:</label>
            <input
              type="number"
              min="1"
              value={currentItem.quantity}
              onChange={(e) => setCurrentItem({ ...currentItem, quantity: parseInt(e.target.value) })}
            />
          </div>

          <div className="form-group">
            <label>Price:</label>
            <input
              type="number"
              min="0"
              step="0.01"
              value={currentItem.price}
              onChange={(e) => setCurrentItem({ ...currentItem, price: parseFloat(e.target.value) })}
            />
          </div>

          <div className="form-group">
            <label>Special Instructions:</label>
            <input
              type="text"
              value={currentItem.specialInstructions}
              onChange={(e) => setCurrentItem({ ...currentItem, specialInstructions: e.target.value })}
            />
          </div>

          <button type="button" onClick={addItem} className="secondary-button">
            Add Item
          </button>
        </div>

        <div className="order-items">
          <h4>Order Items ({order.items.length})</h4>
          {order.items.map((item, index) => (
            <div key={index} className="order-item">
              {item.itemName} x {item.quantity} - ${item.price.toFixed(2)}
            </div>
          ))}
          <div className="total-amount">
            <strong>Total: ${order.totalAmount.toFixed(2)}</strong>
          </div>
        </div>

        <button type="submit" className="primary-button" disabled={order.items.length === 0}>
          Create Order
        </button>
      </form>
    </div>
  );
};

export default OrderForm;
