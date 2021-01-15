import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { WebappSharedModule } from 'app/shared/shared.module';
import { CertificateComponent } from './certificate.component';
import { CertificateDetailComponent } from './certificate-detail.component';
import { CertificateUpdateComponent } from './certificate-update.component';
import { CertificateDeleteDialogComponent } from './certificate-delete-dialog.component';
import { certificateRoute } from './certificate.route';
import { UploadCertificateComponent } from './upload-certificate/upload-certificate.component';
import { OtpComponent } from './otp/otp.component';
import { CertPINComponent } from 'app/entities/certificate/pin/certificate-pin.component';

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild(certificateRoute)],
  declarations: [
    CertificateComponent,
    CertificateDetailComponent,
    CertificateUpdateComponent,
    CertificateDeleteDialogComponent,
    UploadCertificateComponent,
    OtpComponent,
    CertPINComponent,
  ],
  entryComponents: [CertificateDeleteDialogComponent],
})
export class WebappCertificateModule {}
