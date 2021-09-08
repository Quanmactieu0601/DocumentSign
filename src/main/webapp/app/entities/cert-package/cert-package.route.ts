import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ICertPackage, CertPackage } from 'app/shared/model/cert-package.model';
import { CertPackageService } from './cert-package.service';
import { CertPackageComponent } from './cert-package.component';
import { CertPackageDetailComponent } from './cert-package-detail.component';
import { CertPackageUpdateComponent } from './cert-package-update.component';

@Injectable({ providedIn: 'root' })
export class CertPackageResolve implements Resolve<ICertPackage> {
  constructor(private service: CertPackageService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICertPackage> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((certPackage: HttpResponse<CertPackage>) => {
          if (certPackage.body) {
            return of(certPackage.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new CertPackage());
  }
}

export const certPackageRoute: Routes = [
  {
    path: '',
    component: CertPackageComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'webappApp.certPackage.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CertPackageDetailComponent,
    resolve: {
      certPackage: CertPackageResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'webappApp.certPackage.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CertPackageUpdateComponent,
    resolve: {
      certPackage: CertPackageResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'webappApp.certPackage.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CertPackageUpdateComponent,
    resolve: {
      certPackage: CertPackageResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'webappApp.certPackage.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
