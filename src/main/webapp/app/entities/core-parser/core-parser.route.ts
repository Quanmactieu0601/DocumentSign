import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ICoreParser, CoreParser } from 'app/shared/model/core-parser.model';
import { CoreParserService } from './core-parser.service';
import { CoreParserComponent } from './core-parser.component';
import { CoreParserDetailComponent } from './core-parser-detail.component';

@Injectable({ providedIn: 'root' })
export class CoreParserResolve implements Resolve<ICoreParser> {
  constructor(private service: CoreParserService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICoreParser> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((coreParser: HttpResponse<CoreParser>) => {
          if (coreParser.body) {
            return of(coreParser.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new CoreParser());
  }
}

export const coreParserRoute: Routes = [
  {
    path: '',
    component: CoreParserComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'webappApp.coreParser.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CoreParserDetailComponent,
    resolve: {
      coreParser: CoreParserResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'webappApp.coreParser.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
