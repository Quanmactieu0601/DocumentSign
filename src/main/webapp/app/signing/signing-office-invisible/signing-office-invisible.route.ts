import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Routes } from '@angular/router';
import { IUser, User } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { Observable, of } from 'rxjs';
import { SigningOfficeInvisibleComponent } from 'app/signing/signing-office-invisible/signing-office-invisible.component';

@Injectable({ providedIn: 'root' })
export class SigningOfficeInvisibleResolve implements Resolve<IUser> {
  constructor(private service: UserService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUser> {
    const id = route.params['login'];
    if (id) {
      return this.service.find(id);
    }
    return of(new User());
  }
}

export const signingOfficeInvisible: Routes = [
  {
    path: '',
    component: SigningOfficeInvisibleComponent,
  },
];
