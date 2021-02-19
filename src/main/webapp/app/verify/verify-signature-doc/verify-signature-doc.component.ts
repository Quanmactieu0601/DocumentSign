import {Component, OnInit} from '@angular/core';
import {Account} from "app/core/user/account.model";
import {Subscription} from "rxjs";
import {ISignatureVfDTO} from "app/shared/model/signatureVfDTO.model";
import {AccountService} from "app/core/auth/account.service";
import {ToastrService} from "ngx-toastr";
import {HttpEventType, HttpResponse} from "@angular/common/http";
import {VerifySignatureService} from "app/verify/verify-signature.service";

@Component({
  selector: 'jhi-verify-signature-doc',
  templateUrl: './verify-signature-doc.component.html',
  styleUrls: ['./verify-signature-doc.component.scss']
})
export class VerifySignatureDocComponent implements OnInit {
  selectFiles: any;
  progress = 0;
  currentFile: any;
  account: Account | null = null;
  authSubscription?: Subscription;
  fileName: string | undefined;
  signatureVfDTOs?: ISignatureVfDTO[];
  elementsOfIssuer?: string[];
  elementsOfSubjectDN?: string[];

  constructor(private accountService: AccountService,
              private verifySignatureService: VerifySignatureService,
              private toastrService: ToastrService) {
  }

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
  }

  selectFile(event: any): void {
    this.selectFiles = event.target.files;
    this.fileName = event.target.files[0].name;
  }

  verifyDoc(): void {
    this.progress = 0;
    this.currentFile = this.selectFiles.item(0);
    if (!this.currentFile.name.endsWith('doc')
      && !(this.currentFile.name.endsWith('docx'))) this.toastrService.error("Not a doc");
    else if (this.account != null) {
      this.verifySignatureService.verifyDoc(this.currentFile).subscribe(
        (res: any) => {
          if (res.type === HttpEventType.UploadProgress) {
            this.progress = Math.round((100 * res.loaded) / res.total);
          } else if (res instanceof HttpResponse) {
            console.error(res.body.data);
            if (res.status === 200) {
              this.signatureVfDTOs = res.body.data.signatureVfDTOs;
              if (this.signatureVfDTOs == null) this.toastrService.error('False');
            } else {
              this.toastrService.error('Error');
            }
          }
        });
    }
  }
}
