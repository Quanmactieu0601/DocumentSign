import { Component, OnInit } from '@angular/core';
import { Account } from 'app/core/user/account.model';
import { Subscription } from 'rxjs';
import { ISignatureVfDTO } from 'app/shared/model/signatureVfDTO.model';
import { AccountService } from 'app/core/auth/account.service';
import { ToastrService } from 'ngx-toastr';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { VerifySignatureService } from 'app/verify/verify-signature.service';
import { CaptchaService } from 'app/shared/services/captcha.service';
import { ICaptchaModel } from 'app/shared/model/captcha.model';
import { Md5 } from 'ts-md5';

@Component({
  selector: 'jhi-verify-signature-doc',
  templateUrl: './verify-signature-doc.component.html',
  styleUrls: ['./verify-signature-doc.component.scss'],
})
export class VerifySignatureDocComponent implements OnInit {
  selectFiles: File[] = [];
  progress = 0;
  currentFile?: File;
  account: Account | null = null;
  authSubscription?: Subscription;
  fileName: string | undefined;
  signatureVfDTOs?: ISignatureVfDTO[];
  elementsOfIssuer?: string[];
  elementsOfSubjectDN?: string[];
  captcha?: ICaptchaModel | null;
  text = '';
  img?: any;

  constructor(
    private accountService: AccountService,
    private verifySignatureService: VerifySignatureService,
    private toastrService: ToastrService,
    private captchaService: CaptchaService
  ) {}

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
    this.reloadCaptcha();
  }

  reloadCaptcha(): void {
    this.captchaService.generateCaptcha().subscribe(res => {
      this.captcha = res.body;
      this.img = this.captcha?.captchaImg;
    });
  }

  selectFile(event: any): void {
    if (this.selectFiles.length !== 0) this.removeFile(event);
    this.selectFiles.push(...event.addedFiles);
    this.fileName = this.selectFiles[0].name;
  }

  removeFile(event: any): void {
    this.selectFiles.splice(this.selectFiles.indexOf(event), 1);
  }

  verifyDoc(): void {
    if (Md5.hashStr(this.text) === this.captcha?.captchaText) {
      this.reloadCaptcha();
      this.progress = 0;
      this.currentFile = this.selectFiles[0];
      if (!this.currentFile.name.endsWith('doc') && !this.currentFile.name.endsWith('docx')) this.toastrService.error('Not a doc');
      else if (this.account != null) {
        this.verifySignatureService.verifyDoc(this.currentFile).subscribe((res: any) => {
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
    } else {
      this.toastrService.error('Sai captcha');
      this.reloadCaptcha();
    }
  }
}
