import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, Routes } from '@angular/router';
import { Observable, of } from 'rxjs';

import { User, IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { VerifySignatureDocComponent } from "app/verify/verify-signature-doc/verify-signature-doc.component";

@Injectable({ providedIn: 'root' })
export class VerifySignatureResolve implements Resolve<IUser> {
  constructor(private service: UserService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUser> {
    const id = route.params['login'];
    if (id) {
      return this.service.find(id);
    }
    return of(new User());
  }
}

export const verifySignatureDocRoute: Routes = [
  {
    path: '',
    component: VerifySignatureDocComponent,
  },
];
