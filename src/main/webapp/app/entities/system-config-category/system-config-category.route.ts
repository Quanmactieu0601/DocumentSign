import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ISystemConfigCategory, SystemConfigCategory } from 'app/shared/model/system-config-category.model';
import { SystemConfigCategoryService } from './system-config-category.service';
import { SystemConfigCategoryComponent } from './system-config-category.component';
import { SystemConfigCategoryDetailComponent } from './system-config-category-detail.component';
import { SystemConfigCategoryUpdateComponent } from './system-config-category-update.component';

@Injectable({ providedIn: 'root' })
export class SystemConfigCategoryResolve implements Resolve<ISystemConfigCategory> {
  constructor(private service: SystemConfigCategoryService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISystemConfigCategory> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((systemConfigCategory: HttpResponse<SystemConfigCategory>) => {
          if (systemConfigCategory.body) {
            return of(systemConfigCategory.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new SystemConfigCategory());
  }
}

export const systemConfigCategoryRoute: Routes = [
  {
    path: '',
    component: SystemConfigCategoryComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'webappApp.systemConfigCategory.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SystemConfigCategoryDetailComponent,
    resolve: {
      systemConfigCategory: SystemConfigCategoryResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'webappApp.systemConfigCategory.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SystemConfigCategoryUpdateComponent,
    resolve: {
      systemConfigCategory: SystemConfigCategoryResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'webappApp.systemConfigCategory.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SystemConfigCategoryUpdateComponent,
    resolve: {
      systemConfigCategory: SystemConfigCategoryResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'webappApp.systemConfigCategory.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
