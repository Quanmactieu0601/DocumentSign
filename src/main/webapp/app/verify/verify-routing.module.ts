import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HomeLayoutComponent } from 'app/layouts/home-layout/home-layout.component';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'verify-signature',
        component: HomeLayoutComponent,
        loadChildren: () => import('./verify-signature/verify-signature.module').then(m => m.VerifySignatureModule),
      },
    ]),
  ],
})
export class VerifyRoutingModule {}
