import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router, Routes } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ISignatureTemplate, SignatureTemplate } from 'app/shared/model/signature-template.model';
import { SignatureTemplateService } from './signature-template.service';
import { SignatureTemplateComponent } from './signature-template.component';
import { SignatureTemplateDetailComponent } from './signature-template-detail.component';
import { SignatureTemplateUpdateComponent } from './signature-template-update.component';

@Injectable({ providedIn: 'root' })
export class SignatureTemplateResolve implements Resolve<ISignatureTemplate> {
  constructor(private service: SignatureTemplateService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISignatureTemplate> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((signatureTemplate: HttpResponse<SignatureTemplate>) => {
          if (signatureTemplate.body) {
            return of(signatureTemplate.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new SignatureTemplate());
  }
}

export const signatureTemplateRoute: Routes = [
  {
    path: '',
    component: SignatureTemplateComponent,
    data: {
      authorities: [Authority.ADMIN, Authority.SUPER_ADMIN],
      defaultSort: 'id,asc',
      pageTitle: 'webappApp.signatureTemplate.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SignatureTemplateDetailComponent,
    resolve: {
      signatureTemplate: SignatureTemplateResolve,
    },
    data: {
      authorities: [Authority.ADMIN, Authority.SUPER_ADMIN],
      pageTitle: 'webappApp.signatureTemplate.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SignatureTemplateUpdateComponent,
    resolve: {
      signatureTemplate: SignatureTemplateResolve,
    },
    data: {
      authorities: [Authority.ADMIN, Authority.SUPER_ADMIN],
      pageTitle: 'webappApp.signatureTemplate.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SignatureTemplateUpdateComponent,
    resolve: {
      signatureTemplate: SignatureTemplateResolve,
    },
    data: {
      authorities: [Authority.ADMIN, Authority.SUPER_ADMIN],
      pageTitle: 'webappApp.signatureTemplate.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
