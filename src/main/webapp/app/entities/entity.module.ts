import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HomeComponent } from 'app/home/home.component';
import { HomeLayoutComponent } from 'app/layouts/home-layout/home-layout.component';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: '',
        component: HomeLayoutComponent,
        children: [
          { path: '', redirectTo: 'index', pathMatch: 'full' },
          {
            path: 'index',
            component: HomeComponent,
          },
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
          {
            path: 'system-config',
            loadChildren: () => import('./system-config/system-config.module').then(m => m.WebappSystemConfigModule),
          },
          {
            path: 'transaction-report',
            loadChildren: () => import('../entities/transaction-report/transaction-report.module').then(m => m.TransactionReportModule),
          },
          /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
        ],
      },
    ]),
  ],
})
export class WebappEntityModule {}
