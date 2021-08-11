import { Route } from '@angular/router';

import { MetricsComponent } from './metrics.component';
import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';

export const metricsRoute: Route = {
  path: '',
  component: MetricsComponent,
  data: {
    authorities: [Authority.SUPER_ADMIN],
    pageTitle: 'metrics.title',
  },
  canActivate: [UserRouteAccessService],
};
