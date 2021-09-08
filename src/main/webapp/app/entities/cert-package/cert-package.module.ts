import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { WebappSharedModule } from 'app/shared/shared.module';
import { CertPackageComponent } from './cert-package.component';
import { CertPackageDetailComponent } from './cert-package-detail.component';
import { CertPackageUpdateComponent } from './cert-package-update.component';
import { CertPackageDeleteDialogComponent } from './cert-package-delete-dialog.component';
import { certPackageRoute } from './cert-package.route';

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild(certPackageRoute)],
  declarations: [CertPackageComponent, CertPackageDetailComponent, CertPackageUpdateComponent, CertPackageDeleteDialogComponent],
  entryComponents: [CertPackageDeleteDialogComponent],
})
export class WebappCertPackageModule {}
