import { Component, OnInit } from '@angular/core';
import { Account } from 'app/core/user/account.model';
import { Subscription } from 'rxjs';
import { AccountService } from 'app/core/auth/account.service';
import { ToastrService } from 'ngx-toastr';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { ISignatureVfDTO } from 'app/shared/model/signatureVfDTO.model';
import { VerifySignatureService } from 'app/verify/verify-signature.service';

@Component({
  selector: 'jhi-verify-signature',
  templateUrl: './verify-signature-pdf.component.html',
  styleUrls: [],
})
export class VerifySignaturePdfComponent implements OnInit {
  selectFiles: any;
  progress = 0;
  currentFile: any;
  account: Account | null = null;
  authSubscription?: Subscription;
  fileName: string | undefined;
  signatureVfDTOs?: ISignatureVfDTO[];

  constructor(
    private accountService: AccountService,
    private verifySignatureService: VerifySignatureService,
    private toastrService: ToastrService
  ) {}

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
  }

  selectFile(event: any): void {
    this.selectFiles = event.target.files;
    this.fileName = event.target.files[0].name;
  }

  verifyPdf(): void {
    this.progress = 0;
    this.currentFile = this.selectFiles.item(0);
    if (!this.currentFile.name.endsWith('pdf') && !this.currentFile.name.endsWith('pdf')) this.toastrService.error('Select a pdf');
    else if (this.account != null) {
      this.verifySignatureService.verifyPdf(this.currentFile).subscribe((res: any) => {
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

  // changeGender(event: any): void {
  //   this.typeVf = event.target.value;
  // }
}
