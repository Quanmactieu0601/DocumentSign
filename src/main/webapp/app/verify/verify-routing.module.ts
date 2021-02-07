import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {HomeLayoutComponent} from 'app/layouts/home-layout/home-layout.component';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: '',
        component: HomeLayoutComponent,
        children: [
          {
            path: 'verify-signature-pdf',
            loadChildren: () => import('./verify-signature-pdf/verify-signature-pdf.module').then(m => m.VerifySignaturePdfModule),
          },
          {
            path: 'verify-signature-doc',
            loadChildren: () => import('./verify-signature-doc/verify-signature-doc.module').then(m => m.VerifySignatureDocModule)
          }
        ]
      },
    ]),
  ],
})
export class VerifyRoutingModule {
}
