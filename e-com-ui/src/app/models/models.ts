export interface Product {
  id: number,
  name: string,
  description: string,
  price: number,
  quantity: number
}

export interface Page<T> {
  content: T[]; // List of items
  totalPages: number; // Total number of pages
  totalElements: number; // Total number of elements across pages
  size: number; // Number of items per page
  number: number; // Current page number (zero-based)
  numberOfElements: number; // Number of items on the current page
  first: boolean; // Whether this is the first page
  last: boolean; // Whether this is the last page
  empty: boolean; // Whether the page is empty
}

export interface ProductPage extends Page<Product> {}

export interface OrderPage extends Page<Order> {}


export interface Order {
  id: number;
  orderDate: Date;
  orderState: OrderState;
  products: Product[];
}

export enum OrderState {
  NEW,
  PROCESSING,
  DELIVERED,
  CANCELLED
}
