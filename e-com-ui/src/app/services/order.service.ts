import {inject, Injectable} from '@angular/core';
import {Product} from '../models/product.model';
import {ShoppingCart} from '../models/shopping-cart.model';
import Keycloak from 'keycloak-js';
import {NgxIndexedDBService} from 'ngx-indexed-db';
import {ShoppingCartItem} from '../models/shoppingCartItems.model';
import {ToastrService} from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  public readonly keycloak = inject(Keycloak);
  private dbService: NgxIndexedDBService = inject(NgxIndexedDBService);
  private indexedDbName: string | undefined = this.keycloak.tokenParsed?.preferred_username;
  private toastr: ToastrService = inject(ToastrService);

  shoppingCart!: ShoppingCart;

  initCartState(){
    this.shoppingCart = {
      cardProductList: [],
      totalPrice: 0,
      totalItems: 0,
      totalQuantity: 0,
      shippingCost: 0
    };

    if (this.indexedDbName)
      this.dbService.getAll<ShoppingCartItem>(this.indexedDbName).subscribe({
        next: items => {
          if (items.length > 0){
            this.shoppingCart.cardProductList = [...items]
            items.forEach(i => {
              this.shoppingCart.totalPrice += i.price * i.quantityInCart
              this.shoppingCart.totalQuantity += i.quantityInCart
              this.shoppingCart.totalItems++
              this.shoppingCart.shippingCost = this.shoppingCart.totalPrice * 0.0015
            })
          }
        }
      })
  }

  /**
   * Adds a product to the shopping cart with the specified quantity. If the product
   * already exists in the cart, the quantity is updated; otherwise, the product is added
   * as a new entry.
   *
   * @return {void} Does not return any value.
   * @param product
   */
  addToCart(product: Product): void {
    if(this.indexedDbName){
      let productToPush : ShoppingCartItem = {...product, quantityInCart: 1, quantityInStorage: product.quantity}
      this.dbService.add(this.indexedDbName, productToPush).subscribe({
        next: () => {
          this.shoppingCart.cardProductList.push(productToPush)
          this.updateCartSummary(productToPush.price, 1)
          this.toastr.info('Product ' + product.name + ' added to your cart.')
        },
        error: err =>  console.error("Error adding item to db.", err)
      })
    }


  }

  updateCartItem(cartItem: ShoppingCartItem, quantity: number){
    if(this.indexedDbName && cartItem.quantityInCart + quantity <= cartItem.quantityInStorage && cartItem.quantityInCart + quantity >= 0){
      cartItem.quantityInCart += quantity
      this.dbService.update<ShoppingCartItem>(this.indexedDbName, cartItem).subscribe({
        next: () => {
          let itemIndex = this.shoppingCart.cardProductList.findIndex((item) => item.id === cartItem.id);
          console.log("index: " + itemIndex)
          this.shoppingCart.cardProductList[itemIndex] = {...cartItem}
          console.log("item: ", this.shoppingCart.cardProductList[itemIndex])
          this.updateCartSummary(cartItem.price, quantity)

          this.toastr.info('Product ' + cartItem.name + ' added to your cart.')
        },
        error: err =>  console.error("Error updating item to db.", err)
      })
    }
  }

  setNewItemQuantity(item: ShoppingCartItem, quantity: number){
    if(this.indexedDbName && quantity <= item.quantityInStorage && quantity >= 0){
      item.quantityInCart = quantity
      this.dbService.update<ShoppingCartItem>(this.indexedDbName, item).subscribe({
        next: () => console.log("Update success"),
        error: () => this.toastr.error("Error changing item quantity", "Error")
      })
    }
  }

  /**
   * Updates the shopping cart summary including total price, total items, and total quantity.
   *
   * @param {number} unitPrice - The price of the product to add or update.
   * @param {number} unitQuantity - The quantity of the product to add or update.
   * @return {void} This method does not return a value.
   */
  private updateCartSummary(unitPrice: number, unitQuantity: number) : void{
    this.shoppingCart.totalPrice += unitPrice * unitQuantity
    this.shoppingCart.totalQuantity += unitQuantity
    this.shoppingCart.totalItems = this.shoppingCart.cardProductList.length
    this.shoppingCart.shippingCost = this.shoppingCart.totalPrice * 0.0015;
  }

  resetShoppingCart(): void {

    if (this.indexedDbName)
      this.dbService.clear(this.indexedDbName).subscribe({
        next: () => {
          this.shoppingCart = {
            cardProductList: [],
            totalPrice: 0,
            totalItems: 0,
            totalQuantity: 0,
            shippingCost: 0
          };

          this.toastr.info("Shopping cart has been rest.", "Cart update")
        }, error: () => {
          this.toastr.error("Oops! Shopping cart hasn't been rest", "Error")
        }
      })
  }

  removeProductFromCart(product: ShoppingCartItem, i: number) {
    if (this.indexedDbName) this.dbService.deleteByKey(this.indexedDbName, product.id).subscribe({
      next: () => {
        this.shoppingCart.cardProductList.splice(i, 1);
        this.shoppingCart.totalItems = this.shoppingCart.cardProductList.length;
        this.shoppingCart.totalQuantity -= product.quantityInCart;
        this.shoppingCart.totalPrice -= product.price * product.quantityInCart;
        this.toastr.warning("Product deleted from shopping cart", "Cart update")
      },
      error: () => this.toastr.error("Oops! Product couldn't be deleted from shopping cart ", "Error")
    })
  }
}
