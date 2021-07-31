import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Routes } from '@angular/router';
import { Observable, of } from 'rxjs';

import { IUser, User } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { UserManagementComponent } from './user-management.component';
import { UserManagementDetailComponent } from './user-management-detail.component';
import { UserManagementUpdateComponent } from './user-management-update.component';
import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { UserManagementDeleteDialogComponent } from 'app/admin/user-management/user-management-delete-dialog.component';

@Injectable({ providedIn: 'root' })
export class UserManagementResolve implements Resolve<IUser> {
  constructor(private service: UserService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUser> {
    const id = route.params['login'];
    if (id) {
      return this.service.find(id);
    }
    return of(new User());
  }
}

export const userManagementRoute: Routes = [
  {
    path: '',
    component: UserManagementComponent,
    data: {
      authorities: [Authority.ADMIN, Authority.SUPER_ADMIN],
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':login/view',
    component: UserManagementDetailComponent,
    resolve: {
      user: UserManagementResolve,
    },
  },
  {
    path: 'new',
    component: UserManagementUpdateComponent,
    resolve: {
      user: UserManagementResolve,
    },
    data: {
      authorities: [Authority.ADMIN, Authority.SUPER_ADMIN],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':login/edit',
    component: UserManagementUpdateComponent,
    resolve: {
      user: UserManagementResolve,
    },
    data: {
      authorities: [Authority.ADMIN, Authority.SUPER_ADMIN],
    },
    canActivate: [UserRouteAccessService],
  },
];
