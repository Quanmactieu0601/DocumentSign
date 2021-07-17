import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { errorRoute } from './layouts/error/error.route';
import { DEBUG_INFO_ENABLED } from 'app/app.constants';
import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { loginRoute } from 'app/login/login.route';

const LAYOUT_ROUTES = [loginRoute, ...errorRoute];

@NgModule({
  imports: [
    RouterModule.forRoot(
      [
        {
          path: 'admin',
          data: {
            authorities: [Authority.SUPER_ADMIN, Authority.ADMIN, Authority.SIGN, Authority.USER],
          },
          canActivate: [UserRouteAccessService],
          loadChildren: () => import('./admin/admin-routing.module').then(m => m.AdminRoutingModule),
        },
        { path: '', redirectTo: 'home', pathMatch: 'full' },
        {
          path: 'home',
          data: {
            authorities: [Authority.SUPER_ADMIN, Authority.ADMIN, Authority.USER],
          },
          canActivate: [UserRouteAccessService],
          loadChildren: () => import('./entities/entity.module').then(m => m.WebappEntityModule),
        },
        {
          path: 'verify',
          data: {
            authorities: [Authority.USER, Authority.VERIFY, Authority.ADMIN, Authority.SUPER_ADMIN],
          },
          canActivate: [UserRouteAccessService],
          loadChildren: () => import('./verify/verify-routing.module').then(m => m.VerifyRoutingModule),
        },
        {
          path: 'account',
          loadChildren: () => import('./account/account.module').then(m => m.AccountModule),
        },
        ...LAYOUT_ROUTES,
      ],
      { enableTracing: DEBUG_INFO_ENABLED }
    ),
  ],
  exports: [RouterModule],
})
export class WebappAppRoutingModule {}
