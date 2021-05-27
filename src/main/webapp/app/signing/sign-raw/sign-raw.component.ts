import { Component, OnInit } from '@angular/core';
import { AccountService } from 'app/core/auth/account.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { SigningService } from 'app/core/signing/signing.service';
import { FormBuilder } from '@angular/forms';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ICertificate } from 'app/shared/model/certificate.model';
import { Subscription } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { Account } from 'app/core/user/account.model';

@Component({
  selector: 'jhi-sign-raw',
  templateUrl: './sign-raw.component.html',
  styleUrls: ['./sign-raw.component.scss'],
})
export class SignRawComponent implements OnInit {
  textToSign: String = '';
  textResult: String = '';
  listCertificate?: ICertificate[];
  authSubscription?: Subscription;
  account: Account | null = null;
  signingForm = this.fb.group({
    pin: [],
    serial: [],
    otpCode: [],
  });
  hide = true;
  constructor(
    private accountService: AccountService,
    private toastrService: ToastrService,
    private translateService: TranslateService,
    private signingService: SigningService,
    private fb: FormBuilder,
    private certificateService: CertificateService
  ) {}

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
    this.getListCertificate();
  }
  getListCertificate(): void {
    this.certificateService.query().subscribe((res: HttpResponse<ICertificate[]>) => (this.listCertificate = res.body || []));
  }

  signRaw(): void {
    const request = {
      signingRequestContents: [
        {
          data: this.textToSign,
          documentName: '123',
        },
      ],
      tokenInfo: {
        pin: this.signingForm.get(['pin'])!.value,
        serial: this.signingForm.get(['serial'])!.value,
      },
      optional: {
        otpCode: '621143',
      },
    };
    this.signingService.signRaw(request).subscribe(
      (res: any) => {
        const resStatus = JSON.parse(res).status;
        if (resStatus === 0) {
          this.textToSign = JSON.parse(res).data.responseContentList[0].signedDocument;
          const byteArray = this.base64ToArrayBuffer(this.textToSign);
          saveAs(new Blob([byteArray], { type: 'application/raw' }), Date.now().toString());
          this.toastrService.success(this.translateService.instant('sign.messages.signingSuccessful'));
        } else {
          this.toastrService.error(this.translateService.instant('sign.messages.signingFail') + JSON.parse(res).msg);
        }
      },
      () => this.toastrService.error(this.translateService.instant('sign.messages.validate.signingFail.required'))
    );

    this.hide = false;
  }
  arrayBufferToBase64(buffer: any): string {
    return btoa(new Uint8Array(buffer).reduce((data, byte) => data + String.fromCharCode(byte), ''))
      .toString()
      .replace('data:application/xml;base64,', '');
  }

  base64ToArrayBuffer(base64: any): ArrayBuffer {
    const binaryString = window.atob(base64);
    const len = binaryString.length;
    const bytes = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      bytes[i] = binaryString.charCodeAt(i);
    }
    return bytes.buffer;
  }
}
