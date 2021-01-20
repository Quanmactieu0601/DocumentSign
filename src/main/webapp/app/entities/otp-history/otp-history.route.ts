import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IOtpHistory, OtpHistory } from 'app/shared/model/otp-history.model';
import { OtpHistoryService } from './otp-history.service';
import { OtpHistoryComponent } from './otp-history.component';
import { OtpHistoryDetailComponent } from './otp-history-detail.component';
import { OtpHistoryUpdateComponent } from './otp-history-update.component';

@Injectable({ providedIn: 'root' })
export class OtpHistoryResolve implements Resolve<IOtpHistory> {
  constructor(private service: OtpHistoryService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOtpHistory> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((otpHistory: HttpResponse<OtpHistory>) => {
          if (otpHistory.body) {
            return of(otpHistory.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new OtpHistory());
  }
}

export const otpHistoryRoute: Routes = [
  {
    path: '',
    component: OtpHistoryComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'webappApp.otpHistory.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OtpHistoryDetailComponent,
    resolve: {
      otpHistory: OtpHistoryResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'webappApp.otpHistory.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OtpHistoryUpdateComponent,
    resolve: {
      otpHistory: OtpHistoryResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'webappApp.otpHistory.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OtpHistoryUpdateComponent,
    resolve: {
      otpHistory: OtpHistoryResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'webappApp.otpHistory.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
