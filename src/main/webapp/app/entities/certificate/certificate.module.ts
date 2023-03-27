import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { WebappSharedModule } from 'app/shared/shared.module';
import { CertificateComponent } from './certificate.component';
import { CertificateDetailComponent } from './certificate-detail.component';
import { CertificateUpdateComponent } from './certificate-update.component';
import { CertificateDeleteDialogComponent } from './certificate-delete-dialog.component';
import { certificateRoute } from './certificate.route';
import { InstallCertToHsmComponent } from './install-cert-to-hsm/install-cert-to-hsm.component';
import { OtpComponent } from './otp/otp.component';
import { UploadP12CertificateComponent } from './upload-p12-certificate/upload-p12-certificate.component';
import { UploadSignatureImageComponent } from './upload-signature-image/upload-signature-image.component';
import { CertPINComponent } from 'app/entities/certificate/pin/certificate-pin.component';
import { CertificateDeactiveDialogComponent } from 'app/entities/certificate/certificate-deactive-dialog.component';
import { ExportSerialComponent } from 'app/entities/certificate/export-serial/export-serial.component';
import { GenerateCsrComponent } from 'app/entities/certificate/generate-csr/generate-csr.component';
import { ChangeOwnerIdComponent } from './change-owner-id/change-owner-id.component';
import { UploadExcelRegisterComponent } from './upload-excel-register/upload-excel-register.component';
import { ImagePersonalIdComponent } from './image-personal-id/image-personal-id.component';
import { ExportCertReportComponent } from './export-cert-report/export-cert-report.component';

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild(certificateRoute)],
  declarations: [
    CertificateComponent,
    CertificateDetailComponent,
    CertificateUpdateComponent,
    CertificateDeleteDialogComponent,
    InstallCertToHsmComponent,
    OtpComponent,
    UploadP12CertificateComponent,
    UploadSignatureImageComponent,
    CertPINComponent,
    CertificateDeactiveDialogComponent,
    ExportSerialComponent,
    GenerateCsrComponent,
    ChangeOwnerIdComponent,
    UploadExcelRegisterComponent,
    ImagePersonalIdComponent,
    ExportCertReportComponent,
  ],
  entryComponents: [CertificateDeleteDialogComponent],
})
export class WebappCertificateModule {}
