import { Routes } from '@angular/router';

import { ErrorComponent } from './error.component';
import { HomeLayoutComponent } from 'app/layouts/home-layout/home-layout.component';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';

export const errorRoute: Routes = [
  {
    path: '',
    component: HomeLayoutComponent,
    canActivate: [UserRouteAccessService],
    children: [
      {
        path: 'error',
        component: ErrorComponent,
        data: {
          authorities: [],
          pageTitle: 'error.title',
          icon: 'fa fa-exclamation-circle',
          color: 'orange',
        },
      },
      {
        path: 'accessdenied',
        component: ErrorComponent,
        data: {
          authorities: [],
          pageTitle: 'error.title',
          errorMessage: 'error.http.403',
          icon: 'fa fa-exclamation-circle',
          color: 'orange',
        },
      },
      {
        path: '404',
        component: ErrorComponent,
        data: {
          authorities: [],
          pageTitle: 'error.title',
          errorMessage: 'error.http.404',
          icon: 'fa fa-exclamation-circle',
          color: 'orange',
        },
      },
      {
        path: 'developing',
        component: ErrorComponent,
        data: {
          authorities: [],
          pageTitle: 'error.developing',
          icon: 'fa fa-cogs',
          color: 'orange',
        },
      },
      {
        path: '**',
        redirectTo: '/404',
      },
    ],
  },
];
