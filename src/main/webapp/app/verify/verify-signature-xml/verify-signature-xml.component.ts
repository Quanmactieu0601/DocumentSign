import { Component, OnInit } from '@angular/core';
import { Account } from 'app/core/user/account.model';
import { Subscription } from 'rxjs';
import { ISignatureVfDTO } from 'app/shared/model/signatureVfDTO.model';
import { ICaptchaModel } from 'app/shared/model/captcha.model';
import { AccountService } from 'app/core/auth/account.service';
import { VerifySignatureService } from 'app/verify/verify-signature.service';
import { ToastrService } from 'ngx-toastr';
import { CaptchaService } from 'app/shared/services/captcha.service';
import { TranslateService } from '@ngx-translate/core';
import { Md5 } from 'ts-md5';
import { HttpResponse } from '@angular/common/http';

@Component({
  selector: 'jhi-verify-signature-xml',
  templateUrl: './verify-signature-xml.component.html',
  styleUrls: ['./verify-signature-xml.component.scss'],
})
export class VerifySignatureXmlComponent implements OnInit {
  selectFiles: File[] = [];
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
  disable = false;
  constructor(
    private accountService: AccountService,
    private verifySignatureService: VerifySignatureService,
    private toastrService: ToastrService,
    private captchaService: CaptchaService,
    private translateService: TranslateService
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

  isValidCaptcha(): boolean {
    const currentCaptcha = this.captcha?.captchaText;
    this.reloadCaptcha();
    return Md5.hashStr(this.text) === currentCaptcha;
  }

  verifyXml(): void {
    if (!this.isValidCaptcha()) {
      this.toastrService.error(this.translateService.instant('error.validCaptcha'));
      return;
    }
    this.reloadCaptcha();
    this.currentFile = this.selectFiles[0];
    if (this.account != null) {
      this.verifySignatureService.verifyXml(this.currentFile).subscribe((res: any) => {
        if (res instanceof HttpResponse) {
          if (res.body.status === 0) {
            this.signatureVfDTOs = res.body.data.signatureVfDTOs;
            if (!this.signatureVfDTOs?.length) this.toastrService.error(this.translateService.instant('error.verifySign'));
            else this.disable = true;
          } else {
            this.toastrService.error(res.body.msg);
          }
        }
      });
    }
  }
}
