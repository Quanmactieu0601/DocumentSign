import { Route } from '@angular/router';

import { HomeComponent } from './home.component';
import { LoginComponent } from '../login/login.component';
import { UserRouteAccessService } from '../core/auth/user-route-access.service';

export const HOME_ROUTE: Route = {
  path: '',
  // canActivate: [UserRouteAccessService],
  component: HomeComponent,
  data: {
    pageTitle: 'home.title',
  },
};
