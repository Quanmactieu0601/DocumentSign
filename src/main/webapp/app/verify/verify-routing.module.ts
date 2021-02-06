import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'verify-signature',
        loadChildren: () => import('./verify-signature/verify-signature.module').then(m => m.VerifySignatureModule),
      },
    ]),
  ],
})
export class VerifyRoutingModule {}
