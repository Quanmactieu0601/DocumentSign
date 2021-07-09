import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { signingRoute } from 'app/signing/signing.route';
import { NgxDropzoneModule } from 'ngx-dropzone';
import { WebappSharedModule } from 'app/shared/shared.module';
import { SigningOfficeInvisibleComponent } from 'app/signing/signing-office-invisible/signing-office-invisible.component';
import { SigningPdfVisibleComponent } from 'app/signing/signing-pdf-visible/signing-pdf-visible.component';
import { ArchwizardModule } from 'angular-archwizard';
import { PdfViewerModule } from 'ng2-pdf-viewer';
import { NgxExtendedPdfViewerModule } from 'ngx-extended-pdf-viewer';
import { PdfViewComponent } from 'app/signing/signing-pdf-visible/pdf-view/pdf-view.component';
import { SignatureListComponent } from 'app/signing/signing-pdf-visible/pdf-view/signature-list/signature-list.component';
import { SignatureImageViewComponent } from 'app/signing/signing-pdf-visible/signature-image-view/signature-image-view.component';
import { UploadDocComponent } from 'app/signing/signing-pdf-visible/upload-doc/upload-doc.component';

@NgModule({
  imports: [
    RouterModule.forChild(signingRoute),
    NgxDropzoneModule,
    PdfViewerModule,
    ArchwizardModule,
    WebappSharedModule,
    NgxExtendedPdfViewerModule,
  ],
  declarations: [
    SigningOfficeInvisibleComponent,
    SigningPdfVisibleComponent,
    PdfViewComponent,
    SignatureListComponent,
    SignatureImageViewComponent,
    UploadDocComponent,
  ],
  exports: [UploadDocComponent, SignatureImageViewComponent, PdfViewComponent],
})
export class SigningModule {}
