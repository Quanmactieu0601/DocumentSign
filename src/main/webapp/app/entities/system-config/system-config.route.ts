import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ISystemConfig, SystemConfig } from 'app/shared/model/system-config.model';
import { SystemConfigService } from './system-config.service';
import { SystemConfigComponent } from './system-config.component';
import { SystemConfigDetailComponent } from './system-config-detail.component';
import { SystemConfigUpdateComponent } from './system-config-update.component';

@Injectable({ providedIn: 'root' })
export class SystemConfigResolve implements Resolve<ISystemConfig> {
  constructor(private service: SystemConfigService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISystemConfig> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((systemConfig: HttpResponse<SystemConfig>) => {
          if (systemConfig.body) {
            return of(systemConfig.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new SystemConfig());
  }
}

export const systemConfigRoute: Routes = [
  {
    path: '',
    component: SystemConfigComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'webappApp.systemConfig.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SystemConfigDetailComponent,
    resolve: {
      systemConfig: SystemConfigResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'webappApp.systemConfig.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SystemConfigUpdateComponent,
    resolve: {
      systemConfig: SystemConfigResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'webappApp.systemConfig.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SystemConfigUpdateComponent,
    resolve: {
      systemConfig: SystemConfigResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'webappApp.systemConfig.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
