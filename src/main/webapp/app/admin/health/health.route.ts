import { Route } from '@angular/router';

import { HealthComponent } from './health.component';
import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';

export const healthRoute: Route = {
  path: '',
  component: HealthComponent,
  data: {
    authorities: [Authority.SUPER_ADMIN],
    pageTitle: 'health.title',
  },
  canActivate: [UserRouteAccessService],
};
