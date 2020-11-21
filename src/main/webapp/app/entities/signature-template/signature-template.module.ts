import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { WebappSharedModule } from 'app/shared/shared.module';
import { SignatureTemplateComponent } from './signature-template.component';
import { SignatureTemplateDetailComponent } from './signature-template-detail.component';
import { SignatureTemplateUpdateComponent } from './signature-template-update.component';
import { SignatureTemplateDeleteDialogComponent } from './signature-template-delete-dialog.component';
import { signatureTemplateRoute } from './signature-template.route';

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild(signatureTemplateRoute)],
  declarations: [
    SignatureTemplateComponent,
    SignatureTemplateDetailComponent,
    SignatureTemplateUpdateComponent,
    SignatureTemplateDeleteDialogComponent,
  ],
  entryComponents: [SignatureTemplateDeleteDialogComponent],
})
export class WebappSignatureTemplateModule {}
