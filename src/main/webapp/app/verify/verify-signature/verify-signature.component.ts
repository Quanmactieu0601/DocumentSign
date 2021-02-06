import { Component, OnInit } from '@angular/core';
import { Account } from 'app/core/user/account.model';
import { Subscription } from 'rxjs';
import { AccountService } from 'app/core/auth/account.service';
import { ToastrService } from 'ngx-toastr';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { VerifySignatureService } from 'app/verify/verify-signature/verify-signature.service';
import { ISignatureVfDTO } from 'app/shared/model/signatureVfDTO.model';

@Component({
  selector: 'jhi-verify-signature',
  templateUrl: './verify-signature.component.html',
  styleUrls: ['./verify-signature.component.scss'],
})
export class VerifySignatureComponent implements OnInit {
  selectFiles: any;
  progress = 0;
  currentFile: any;
  account: Account | null = null;
  authSubscription?: Subscription;
  fileName: string | undefined;
  signatureVfDTOs?: ISignatureVfDTO[];
  typeVf: string | undefined;

  constructor(
    private accountService: AccountService,
    private verifySignatureService: VerifySignatureService,
    private toastrService: ToastrService
  ) {}

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
    this.typeVf = 'pdf';
  }

  selectFile(event: any): void {
    this.selectFiles = event.target.files;
    this.fileName = event.target.files[0].name;
  }

  verifyPdf(): void {
    this.progress = 0;
    this.currentFile = this.selectFiles.item(0);
    if (this.account != null) {
      if (this.typeVf === 'pdf') {
        console.error('123');
        this.verifySignatureService.verifyPdf(this.currentFile).subscribe((res: any) => {
          if (res.type === HttpEventType.UploadProgress) {
            this.progress = Math.round((100 * res.loaded) / res.total);
          } else if (res instanceof HttpResponse) {
            console.error(res.body.data);
            if (res.status === 200) {
              this.signatureVfDTOs = res.body.data.signatureVfDTOs;
            } else {
              this.toastrService.error('Error');
            }
          }
        });
      } else if (this.typeVf === 'doc') {
        this.verifySignatureService.verifyDoc(this.currentFile).subscribe((res: any) => {
          if (res.type === HttpEventType.UploadProgress) {
            this.progress = Math.round((100 * res.loaded) / res.total);
          } else if (res instanceof HttpResponse) {
            console.error(res.body.data);
            if (res.status === 200) {
              this.signatureVfDTOs = res.body.data.signatureVfDTOs;
            } else {
              this.toastrService.error('Error');
            }
          }
        });
      }
    }
  }

  changeGender(event: any): void {
    this.typeVf = event.target.value;
  }
}
