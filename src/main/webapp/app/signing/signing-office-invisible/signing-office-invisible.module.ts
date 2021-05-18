import {NgModule} from "@angular/core";
import {WebappSharedModule} from "app/shared/shared.module";
import {RouterModule} from "@angular/router";
import {signingOfficeInvisible} from "app/signing/signing-office-invisible/signing-office-invisible.route";
import {NgxDropzoneModule} from "ngx-dropzone";
import {SigningOfficeInvisibleComponent} from "app/signing/signing-office-invisible/signing-office-invisible.component";

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild(signingOfficeInvisible), NgxDropzoneModule],
  declarations: [SigningOfficeInvisibleComponent],
  providers: [],
  bootstrap: [SigningOfficeInvisibleComponent]
})

export class SigningOfficeInvisibleModule {}
