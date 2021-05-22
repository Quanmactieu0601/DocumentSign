import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HomeLayoutComponent } from 'app/layouts/home-layout/home-layout.component';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: '',
        component: HomeLayoutComponent,
        children: [
          {
            path: 'signing-office-invisible',
            loadChildren: () =>
              import('./signing-office-invisible/signing-office-invisible.module').then(m => m.SigningOfficeInvisibleModule),
          },
        ],
      },
    ]),
  ],
})
export class SigningRoutingModule {}
