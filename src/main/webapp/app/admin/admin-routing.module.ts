import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HomeLayoutComponent } from 'app/layouts/home-layout/home-layout.component';
import { HomeComponent } from 'app/home/home.component';
// import { SigningComponent } from 'app/signing/signing.component';
import { Authority } from 'app/shared/constants/authority.constants';
import { SigningPdfVisibleComponent } from 'app/signing/signing-pdf-visible/signing-pdf-visible.component';

import { SignRawComponent } from 'app/signing/sign-raw/sign-raw.component';

/* jhipster-needle-add-admin-module-import - JHipster will add admin modules imports here */

@NgModule({
  imports: [
    /* jhipster-needle-add-admin-module - JHipster will add admin modules here */
    RouterModule.forChild([
      {
        path: '',
        component: HomeLayoutComponent,
        children: [
          { path: '', redirectTo: '', pathMatch: 'full' },
          {
            path: '',
            component: HomeComponent,
            // loadChildren: () => import('../home/home.module').then(m => m.WebappHomeModule)
          },
          {
            path: 'user-management',
            loadChildren: () => import('./user-management/user-management.module').then(m => m.UserManagementModule),
            data: {
              pageTitle: 'userManagement.home.title',
            },
          },
          {
            path: 'audits',
            loadChildren: () => import('./audits/audits.module').then(m => m.AuditsModule),
          },
          {
            path: 'configuration',
            loadChildren: () => import('./configuration/configuration.module').then(m => m.ConfigurationModule),
          },
          {
            path: 'docs',
            loadChildren: () => import('./docs/docs.module').then(m => m.DocsModule),
          },
          {
            path: 'health',
            loadChildren: () => import('./health/health.module').then(m => m.HealthModule),
          },
          {
            path: 'logs',
            loadChildren: () => import('./logs/logs.module').then(m => m.LogsModule),
          },
          {
            path: 'metrics',
            loadChildren: () => import('./metrics/metrics.module').then(m => m.MetricsModule),
          },
          /* jhipster-needle-add-admin-route - JHipster will add admin routes here */
        ],
      },
    ]),
  ],
})
export class AdminRoutingModule {}
