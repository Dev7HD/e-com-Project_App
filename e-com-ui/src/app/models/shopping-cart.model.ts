import {ShoppingCartItem} from './shoppingCartItems.model';

export interface ShoppingCart {
  cardProductList: ShoppingCartItem[];
  totalPrice: number;
  totalItems: number;
  totalQuantity: number;
  shippingCost: number;
}
