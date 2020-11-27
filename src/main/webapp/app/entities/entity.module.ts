import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'certificate',
        loadChildren: () => import('./certificate/certificate.module').then(m => m.WebappCertificateModule),
      },
      {
        path: 'transaction',
        loadChildren: () => import('./transaction/transaction.module').then(m => m.WebappTransactionModule),
      },
      {
        path: 'signature-template',
        loadChildren: () => import('./signature-template/signature-template.module').then(m => m.WebappSignatureTemplateModule),
      },
      {
        path: 'signature-image',
        loadChildren: () => import('./signature-image/signature-image.module').then(m => m.WebappSignatureImageModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class WebappEntityModule {}
