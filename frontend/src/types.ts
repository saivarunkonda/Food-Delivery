export interface Order {
  id: string;
  customerId: string;
  restaurantId: string;
  driverId?: string;
  status: OrderStatus;
  totalAmount: number;
  deliveryAddress: string;
  customerPhone: string;
  items: OrderItem[];
  createdAt: string;
  updatedAt: string;
  estimatedDeliveryTime?: string;
  deliveredAt?: string;
}

export interface OrderItem {
  id: string;
  itemName: string;
  quantity: number;
  price: number;
  specialInstructions?: string;
}

export enum OrderStatus {
  PENDING = 'PENDING',
  ACCEPTED = 'ACCEPTED',
  PREPARING = 'PREPARING',
  READY = 'READY',
  PICKED_UP = 'PICKED_UP',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED'
}

export interface OrderRequest {
  customerId: string;
  restaurantId: string;
  totalAmount: number;
  deliveryAddress: string;
  customerPhone: string;
  items: OrderItemRequest[];
}

export interface OrderItemRequest {
  itemName: string;
  quantity: number;
  price: number;
  specialInstructions?: string;
}
