import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ISignatureImage, SignatureImage } from 'app/shared/model/signature-image.model';
import { SignatureImageService } from './signature-image.service';
import { SignatureImageComponent } from './signature-image.component';
import { SignatureImageDetailComponent } from './signature-image-detail.component';
import { SignatureImageUpdateComponent } from './signature-image-update.component';

@Injectable({ providedIn: 'root' })
export class SignatureImageResolve implements Resolve<ISignatureImage> {
  constructor(private service: SignatureImageService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISignatureImage> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((signatureImage: HttpResponse<SignatureImage>) => {
          if (signatureImage.body) {
            return of(signatureImage.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new SignatureImage());
  }
}

export const signatureImageRoute: Routes = [
  {
    path: '',
    component: SignatureImageComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'webappApp.signatureImage.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SignatureImageDetailComponent,
    resolve: {
      signatureImage: SignatureImageResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'webappApp.signatureImage.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SignatureImageUpdateComponent,
    resolve: {
      signatureImage: SignatureImageResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'webappApp.signatureImage.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SignatureImageUpdateComponent,
    resolve: {
      signatureImage: SignatureImageResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'webappApp.signatureImage.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
