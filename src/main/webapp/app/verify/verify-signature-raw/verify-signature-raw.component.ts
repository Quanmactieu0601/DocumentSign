import { Component, OnInit } from '@angular/core';
import { Account } from 'app/core/user/account.model';
import { Subscription } from 'rxjs';
import { AccountService } from 'app/core/auth/account.service';
import { ToastrService } from 'ngx-toastr';
import { VerifySignatureService } from 'app/verify/verify-signature.service';
import { ISignatureVfVM, SignatureVfVM } from 'app/shared/model/signatureVfVM.model';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ElementVM, IElementVM } from 'app/shared/model/elementVM.model';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ICertificate } from 'app/shared/model/certificate.model';
import { ICaptchaModel } from 'app/shared/model/captcha.model';
import { CaptchaService } from 'app/shared/services/captcha.service';
import { Md5 } from 'ts-md5';
import { TranslateService } from '@ngx-translate/core';
import { ISignatureVfDTO } from 'app/shared/model/signatureVfDTO.model';
import { CertificateVfDTO } from 'app/shared/model/certificateVfDTO.model';

@Component({
  selector: 'jhi-verify-signature',
  templateUrl: './verify-signature-raw.component.html',
  styleUrls: ['./verify-signature-raw.component.scss'],
})
export class VerifySignatureRawComponent implements OnInit {
  account: Account | null = null;
  authSubscription?: Subscription;
  certificate: any;
  signatureVfVM: ISignatureVfVM | undefined;
  elementVM: IElementVM | undefined;
  result = false;
  bar = false;
  certificateVfDTO?: CertificateVfDTO;
  listCertificate?: ICertificate[];
  captcha?: ICaptchaModel | null;
  img?: any;
  editForm = this.fb.group({
    base64Signature: [],
    base64OriginalData: [],
    serial: [],
    text: [],
  });

  constructor(
    private accountService: AccountService,
    private verifySignatureService: VerifySignatureService,
    private toastrService: ToastrService,
    private certificateService: CertificateService,
    private fb: FormBuilder,
    private captchaService: CaptchaService,
    private translateService: TranslateService
  ) {}

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
    this.getListCertificate();
    this.reloadCaptcha();
  }

  getListCertificate(): void {
    this.certificateService.query().subscribe((res: HttpResponse<ICertificate[]>) => (this.listCertificate = res.body || []));
  }

  private createFromForm(): ISignatureVfVM {
    return {
      ...new SignatureVfVM(),
      elements: [
        {
          ...new ElementVM(),
          base64Signature: this.editForm.get(['base64Signature'])!.value,
          base64OriginalData: btoa(unescape(encodeURIComponent(this.editForm.get(['base64OriginalData'])!.value))),
          key: '123',
        },
      ],
      hashAlgorithm: 'SHA1',
      serial: this.editForm.get(['serial'])!.value,
    };
  }

  reloadCaptcha(): void {
    this.captchaService.generateCaptcha().subscribe(res => {
      this.captcha = res.body;
      this.img = this.captcha?.captchaImg;
    });
  }

  isValidCaptcha(): boolean {
    const currentCaptcha = this.captcha?.captchaText;
    this.reloadCaptcha();
    return Md5.hashStr(this.editForm.get(['text'])!.value) === currentCaptcha;
  }

  verifyRaw(): void {
    if (!this.isValidCaptcha()) {
      this.toastrService.error(this.translateService.instant('error.validCaptcha'));
      return;
    }
    this.signatureVfVM = this.createFromForm();
    if (this.account != null) {
      this.verifySignatureService.verifyRaw(this.signatureVfVM).subscribe((res: any) => {
        if (res instanceof HttpResponse) {
          console.error(res.body.data);
          if (res.body.status === 0) {
            this.certificate = res.body.data.certificate;
            this.certificateVfDTO = res.body.data.certificateVfDTO;
            this.result = res.body.data.elements[0].result;
            if (!this.result) this.toastrService.error(this.translateService.instant('error.verifySign'));
          } else {
            this.toastrService.error(res.body.msg);
          }
        }
      });
    }
  }
}
