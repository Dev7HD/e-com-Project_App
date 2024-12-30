import {OrderState} from './order-state.enum';
import {Product} from './product.model';

export interface Order {
  id: number;
  orderDate: Date;
  orderState: OrderState;
  totalPrice: number;
  totalQuantity: number;
  totalItems: number;
  products: Product[];
  customerName: string
}
