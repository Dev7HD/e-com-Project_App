import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {
  AutoRefreshTokenService,
  INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
  includeBearerTokenInterceptor,
  provideKeycloak,
  UserActivityService,
  withAutoRefreshToken
} from 'keycloak-angular';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {CookieService} from 'ngx-cookie-service';
import {provideAnimations} from '@angular/platform-browser/animations';
import {provideToastr} from 'ngx-toastr';
import {DBConfig, ObjectStoreMeta, provideIndexedDb} from 'ngx-indexed-db';

export const provideKeycloakAngular = () =>
  provideKeycloak({
    config: {
      url: 'http://localhost:8080',
      realm: 'e-com-realm',
      clientId: 'e-com-ui_client'
    },
    initOptions: {
      onLoad: 'check-sso',
      silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html'
    },
    features: [
      withAutoRefreshToken({
        onInactivityTimeout: 'logout',
        sessionTimeout: 120000
      })
    ],
    providers: [AutoRefreshTokenService, UserActivityService]
  });

const storeSchema: ObjectStoreMeta = {
  store: 'items',
  storeConfig: { keyPath: 'id', autoIncrement: false },
  storeSchema: [
    { name: 'id', keypath: 'id', options: {unique: true}},
    { name: 'name', keypath: 'name', options: { unique: false } },
    { name: 'description', keypath: 'description', options: { unique: false } },
    { name: 'price', keypath: 'price', options: { unique: false } },
    { name: 'quantityInCart', keypath: 'quantityInCart', options: { unique: false } },
    { name: 'quantityInStorage', keypath: 'quantityInStorage', options: { unique: false } }
  ]
}

const dbConfig: DBConfig  = {
  name: 'ShoppingCartItems',
  version: 10,
  objectStoresMeta: [storeSchema]
};

export const appConfig: ApplicationConfig = {
  providers:
    [
      provideIndexedDb(dbConfig),
      provideAnimations(),
      provideToastr({
        closeButton: true,
        timeOut: 3000,
        progressBar: true,
        progressAnimation: 'increasing',
        positionClass: 'toast-top-right',
        newestOnTop: true,
        tapToDismiss: true,
        maxOpened: 3,
        autoDismiss: true,
        easeTime: 300,

      }),
      CookieService,
      provideZoneChangeDetection({ eventCoalescing: true }), provideRouter(routes), provideAnimationsAsync(),
      provideKeycloakAngular(),
      provideHttpClient(withInterceptors([includeBearerTokenInterceptor])),
      {
        provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
        useValue: [
          {
            urlPattern: /^(http:\/\/localhost:8888)(\/.*)?$/i,
            httpMethods: ['GET', 'POST', 'PATCH', 'DELETE',]
          }
        ]
      }
    ]
};
