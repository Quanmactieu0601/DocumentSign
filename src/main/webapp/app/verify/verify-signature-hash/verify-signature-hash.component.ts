import { Component, HostListener, OnInit } from '@angular/core';
import { Account } from 'app/core/user/account.model';
import { Subscription } from 'rxjs';
import { AccountService } from 'app/core/auth/account.service';
import { ToastrService } from 'ngx-toastr';
import { VerifySignatureService } from 'app/verify/verify-signature.service';
import { ISignatureVfVM, SignatureVfVM } from 'app/shared/model/signatureVfVM.model';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ElementVM, IElementVM } from 'app/shared/model/elementVM.model';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ICertificate } from 'app/shared/model/certificate.model';

import { ICaptchaModel } from 'app/shared/model/captcha.model';
import { CaptchaService } from 'app/shared/services/captcha.service';
import { Md5 } from 'ts-md5';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'jhi-verify-signature',
  templateUrl: './verify-signature-hash.component.html',
  styleUrls: ['./verify-signature-hash.component.scss'],
})
export class VerifySignatureHashComponent implements OnInit {
  account: Account | null = null;
  authSubscription?: Subscription;
  certificate: any;
  signatureVfVM: ISignatureVfVM | undefined;
  elementVM: IElementVM | undefined;
  result = false;
  bar = false;
  listCertificate: ICertificate[] = [];
  captcha?: ICaptchaModel | null;
  img?: any;
  serial = '';
  page = 0;
  timer: NodeJS.Timeout | undefined;
  editForm = this.fb.group({
    base64Signature: ['', Validators.required],
    base64OriginalData: ['', Validators.required],
    serial: ['', Validators.required],
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
    this.getListCertificate('', 0);
    this.reloadCaptcha();
  }

  getListCertificate(s: string, p: number): void {
    const data = {
      page: p,
      size: 20,
      sort: ['id,desc'],
      alias: null,
      ownerId: this.account?.login,
      serial: s,
      validDate: null,
      expiredDate: null,
    };
    if (p === 0) this.listCertificate = [];
    this.certificateService.findCertificate(data).subscribe((res: HttpResponse<ICertificate[]>) => {
      this.listCertificate.push(...(res.body || []));
    });
  }

  @HostListener('scroll', ['$event'])
  getMoreCert(e: any): void {
    if (e.target.scrollHeight === e.target.scrollTop + e.target.clientHeight) {
      this.getListCertificate(this.serial, ++this.page);
    }
  }

  selectSerial(serial: string): void {
    this.editForm.controls['serial'].setValue(serial);
  }

  filter(part: string): void {
    if (this.timer) {
      clearTimeout(this.timer);
    }
    this.timer = setTimeout(() => {
      this.page = 0;
      this.getListCertificate(part, this.page);
    }, 1000);
  }

  private createFromForm(): ISignatureVfVM {
    return {
      ...new SignatureVfVM(),
      elements: [
        {
          ...new ElementVM(),
          base64Signature: this.editForm.get(['base64Signature'])!.value,
          base64OriginalData: btoa(this.editForm.get(['base64OriginalData'])!.value),
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

  verifyHash(): void {
    if (!this.isValidCaptcha()) {
      this.toastrService.error(this.translateService.instant('error.validCaptcha'));
      return;
    }
    this.signatureVfVM = this.createFromForm();
    if (this.account != null) {
      this.verifySignatureService.verifyHash(this.signatureVfVM).subscribe((res: any) => {
        if (res instanceof HttpResponse) {
          console.error(res.body.data);
          if (res.body.status === 0) {
            this.certificate = res.body.data.certificate;
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
