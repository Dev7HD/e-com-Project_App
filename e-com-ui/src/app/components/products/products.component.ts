import {Component, inject, OnInit, signal, TemplateRef, WritableSignal} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Product} from '../../models/product.model';
import {NgForOf, NgIf} from '@angular/common';
import {environment} from '../../../environments/environment.development';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {ModalDismissReasons, NgbAlert, NgbModal, NgbPagination, NgbTooltip} from '@ng-bootstrap/ng-bootstrap';
import {ToastrService} from 'ngx-toastr';
import {ProductPage} from '../../models/product-page.model';
import {OrderService} from '../../services/order.service';
import {UserStateService} from '../../services/user-state.service';
import {NgxIndexedDBService} from 'ngx-indexed-db';
import {ShoppingCartItem} from '../../models/shoppingCartItems.model';
import Keycloak from 'keycloak-js';

@Component({
  selector: 'app-products',
  imports: [
    NgForOf,
    FormsModule,
    NgbPagination,
    NgbAlert,
    NgIf,
    ReactiveFormsModule,
    NgbTooltip,
  ],
  templateUrl: './products.component.html',
  styleUrl: './products.component.css'
})
export class ProductsComponent implements OnInit {
  private _http: HttpClient = inject(HttpClient);
  private formBuilder = inject(FormBuilder);
  private modalService = inject(NgbModal);
  private toastr: ToastrService = inject(ToastrService);
  public readonly keycloak = inject(Keycloak);
  orderService: OrderService = inject(OrderService);
  userStateService: UserStateService = inject(UserStateService);
  private dbService: NgxIndexedDBService = inject(NgxIndexedDBService);
  private indexedDbName: string | undefined = this.keycloak.tokenParsed?.preferred_username;

  products!: ProductPage;
  searchParams: HttpParams = new HttpParams();
  id!: string;
  name!: string;
  description!: string;
  minPrice!: number;
  maxPrice!: number;
  minQuantity!: number;
  maxQuantity!: number;
  page: number = 0;
  size: number = 10;
  isLoading: boolean = false;
  isSaving: boolean = false;
  isEmpty: boolean = true;
  productFormGroup!: FormGroup;
  closeResult: WritableSignal<string> = signal('');
  isDeleting: boolean = false;
  productIdToDelete!: string;
  isEditing: boolean = false;


  ngOnInit() {
    this.intiNewProductFormGroup();
    this.getProducts(new HttpParams())
  }

  search(): void {
    this.appendSearchParam("id", this.id);
    this.appendSearchParam("name", this.name);
    this.appendSearchParam("description", this.description);
    this.appendSearchParam("minPrice", this.minPrice);
    this.appendSearchParam("maxPrice", this.maxPrice);
    this.appendSearchParam("minQuantity", this.minQuantity);
    this.appendSearchParam("maxQuantity", this.maxQuantity);
    this.appendSearchParam("page", 0);
    this.appendSearchParam("size", this.size);

    this.getProducts(this.searchParams);
  }

  private appendSearchParam(key: string, value: any): void {
    if (this.definedAndNotNull(value)) {
      this.searchParams = this.searchParams.has(key)
        ? this.searchParams.set(key, value)
        : this.searchParams.append(key, value);
    }
  }

  private definedAndNotNull(value: any): boolean {
    return value !== undefined && value !== null;
  }

  /**
   * Fetches a list of products from the API based on search parameters.
   *
   * @param {HttpParams} searchParams - The HTTP parameters used for the search query.
   * @return {void} This method does not explicitly return a value; products are updated in the component state.
   */
  getProducts(searchParams: HttpParams) {
    this.isLoading = true;
    this._http.get<ProductPage>(`${environment.productURL}/api/products/search`, { params: searchParams }).subscribe({
      next: data => {
        this.isEmpty = data.content.length === 0;
        this.isLoading = false;
        this.products = data;
        this.page = data.number + 1;
      }, error: error => {
        this.isLoading = false;
        console.log('Error fetching products',error);
      }
    })
  }

  pageChanged($event: number) {
    this.page = $event;
    let currentPage = Number($event-1)
    this.appendSearchParam("page", currentPage);
    this.getProducts(this.searchParams);
  }

  sizeChanged() {
    this.appendSearchParam("size", this.size);
    this.page = 0;
  }

  newProduct(): void{
    this.isSaving = true;
    this._http.post<Product>(`${environment.productURL}/api/products/new`, this.productFormGroup.value).subscribe({
      next: () => {
        this.isSaving = false;
        this.getProducts(this.searchParams);
        this.intiNewProductFormGroup();
        this.modalService.dismissAll();
        this.showSuccess()
      }, error: error => {
        this.isSaving = false;
        this.modalService.dismissAll();
        this.showError();
        console.log('Error creating product',error);
      }
    })
  }

  intiNewProductFormGroup(){
    this.productFormGroup = this.formBuilder.group({
      name: this.formBuilder.control('', Validators.required),
      description: this.formBuilder.control('', Validators.required),
      price: this.formBuilder.control('', [Validators.required, Validators.min(1), Validators.pattern(/^[0-9]*(\.[0-9]{1,3})?$/)]),
      quantity: this.formBuilder.control('', [Validators.required, Validators.min(0), Validators.pattern(/^[0-9]+$/)])
    })
  }

  showModal(content: TemplateRef<any>) {
    this.modalService.open(content, { ariaLabelledBy: 'modal-basic-title' }).result.then(
      (result) => {
        this.closeResult.set(`Closed with: ${result}`);
      },
      (reason) => {
        this.closeResult.set(`Dismissed ${this.getDismissReason(reason)}`);
      },
    );
  }

  private getDismissReason(reason: any): string {
    switch (reason) {
      case ModalDismissReasons.ESC:
        return 'by pressing ESC';
      case ModalDismissReasons.BACKDROP_CLICK:
        return 'by clicking on a backdrop';
      default:
        return `with: ${reason}`;
    }
  }

  showSuccess() {
    this.toastr.success('Product saved successfully', 'Success');
  }

  showError() {
    this.toastr.error('Error creating product', 'Error');
  }

  productIdToEdit!: string;
  showModalEditProduct(product: Product, content: TemplateRef<any>) {
    this.isEditing = true;
    this.productIdToEdit = product.id.toString();
    this.productFormGroup = this.formBuilder.group({
      name: this.formBuilder.control(product.name, Validators.required),
      description: this.formBuilder.control(product.description, Validators.required),
      price: this.formBuilder.control(product.price, [Validators.required, Validators.min(1), Validators.pattern(/^[0-9]*(\.[0-9]{1,3})?$/)]),
      quantity: this.formBuilder.control(product.quantity, [Validators.required, Validators.min(0), Validators.pattern(/^[0-9]+$/)])
    })
    this.showModal(content);
  }


  showModalDeleteProduct(id: string, content: TemplateRef<any>) {
    this.productIdToDelete = id;
    this.showModal(content);
  }

  editProduct(){
    this.isSaving= true;
    this._http.patch<Product>(`${environment.productURL}/api/products/${this.productIdToEdit}/update`, this.productFormGroup.value).subscribe({
      next: data => {
        let index  = this.products.content.findIndex(product => product.id.toString() === this.productIdToEdit);
        console.log(
          'index',
          index)
        this.products.content[index] = data;
        this.isSaving = false;
        this.isEditing = false;
        this.modalService.dismissAll();
        this.showSuccess()
      }, error: () => {
        this.isSaving = false;
        this.isEditing = false;
        this.modalService.dismissAll();
        this.showError();
      }
    })
  }

  deleteProduct() {
    this.isDeleting = true;
    this._http.delete<Product>(`${environment.productURL}/api/products/${this.productIdToDelete}/delete`).subscribe({
      next: () => {
        let index  = this.products.content.findIndex(product => product.id.toString() === this.productIdToDelete);
        this.products.content.splice(index, 1);
        this.isDeleting = false;
        this.modalService.dismissAll();
        this.showSuccess()
      }, error: error => {
        this.isDeleting = false;
        this.modalService.dismissAll();
        this.showError();
        console.log('Error deleting product',error);
      }
    })
  }

  reset() {
    this.searchParams = new HttpParams();
    this.id = String();
    this.name = String() ;
    this.description = String() ;
    this.minPrice = Number();
    this.maxPrice = Number() ;
    this.minQuantity = Number() ;
    this.maxQuantity = Number() ;
    this.page = 0;
    this.size = 10;

    this.getProducts(this.searchParams);
  }

  addToCard(product: Product) {
    if(this.indexedDbName){
      this.dbService.getByKey<ShoppingCartItem>(this.indexedDbName, product.id).subscribe({
        next: data => {
          console.log(data)
          data ? this.orderService.updateCartItem(data, 1) : this.orderService.addToCart(product)
          product.quantity--;
        },
        error: err => console.error("Error getting all data.",err)
      })
    }

  }

}
