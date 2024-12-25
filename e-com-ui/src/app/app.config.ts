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

export const appConfig: ApplicationConfig = {
  providers:
    [
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
