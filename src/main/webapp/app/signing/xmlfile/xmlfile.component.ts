import { Component, ElementRef, OnInit } from '@angular/core';
import { AccountService } from 'app/core/auth/account.service';
import { Subscription } from 'rxjs';
import { Account } from 'app/core/user/account.model';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { HttpResponse } from '@angular/common/http';
import { saveAs } from 'file-saver';
import { FormBuilder } from '@angular/forms';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ICertificate } from 'app/shared/model/certificate.model';
import { SigningService } from 'app/core/signing/signing.service';
import { error } from '@angular/compiler/src/util';
@Component({
  selector: 'jhi-xmlfile',
  templateUrl: './xmlfile.component.html',
  styleUrls: ['./xmlfile.component.scss'],
})
export class XmlfileComponent implements OnInit {
  selectFiles: File[] = [];
  currentFile?: File;
  listCertificate?: ICertificate[];
  authSubscription?: Subscription;
  account: Account | null = null;
  fileName: string | undefined;
  resFile = '';
  disable = false;
  signingForm = this.fb.group({
    pin: [],
    serial: [],
    otpCode: [],
  });
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

  selectFile(event: any): void {
    if (this.selectFiles.length !== 0) this.removeFile(event);
    this.selectFiles.push(...event.addedFiles);
    this.fileName = this.selectFiles[0].name;
  }

  removeFile(event: any): void {
    this.selectFiles.splice(this.selectFiles.indexOf(event), 1);
  }

  signxml(): void {
    this.currentFile = this.selectFiles[0];
    const reader = new FileReader();
    reader.readAsArrayBuffer(this.currentFile);
    reader.onload = () => {
      const request = {
        signingRequestContents: [
          {
            data: this.arrayBufferToBase64(reader.result),
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
      this.signingService.signXml(request).subscribe(
        (res: any) => {
          const resStatus = JSON.parse(res).status;
          if (resStatus === 0) {
            this.resFile = JSON.parse(res).data.responseContentList[0].signedDocument;
            const byteArray = this.base64ToArrayBuffer(this.resFile);
            saveAs(new Blob([byteArray], { type: 'application/xml' }), Date.now().toString());
            this.toastrService.success(this.translateService.instant('sign.messages.signingSuccessful'));
          } else {
            this.toastrService.error(this.translateService.instant('sign.messages.signingFail') + JSON.parse(res).msg);
          }
        },
        () => this.toastrService.error(this.translateService.instant('sign.messages.validate.signingFail.required'))
      );
    };
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
