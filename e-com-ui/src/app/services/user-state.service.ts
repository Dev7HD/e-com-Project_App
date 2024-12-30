import {Injectable, OnInit} from '@angular/core';
import Keycloak from 'keycloak-js';

@Injectable({
  providedIn: 'root'
})
export class UserStateService implements OnInit{

  constructor(private _keycloak: Keycloak) { }

  userState: any = {
    firstName: '',
    lastName: '',
    username: '',
    email: '',
    role: '',
    isAuthenticated: false
  }

  ngOnInit() {
    this.initUserState()
  }

  initUserState(){
    if (this._keycloak.authenticated) {
      this.userState.isAuthenticated = true;
    }
    this.userState.username = this._keycloak.tokenParsed?.preferred_username;
    this.userState.email = this._keycloak.tokenParsed?.email;
    this.userState.firstName = this._keycloak.tokenParsed?.given_name;
    this.userState.lastName = this._keycloak.tokenParsed?.family_name;
    this.userState.role = this._keycloak.hasRealmRole('ADMIN') ? 'ADMIN' : 'CLIENT';
    console.table(this.userState)
  }
}
