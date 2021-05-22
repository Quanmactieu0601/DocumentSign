import { Component, OnInit } from '@angular/core';
import { AccountService } from 'app/core/auth/account.service';
import { Subscription } from 'rxjs';
import { Account } from 'app/core/user/account.model';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { SigningService } from 'app/signing/signing.service';
import { HttpResponse } from '@angular/common/http';
import { saveAs } from 'file-saver';
import { FormBuilder } from '@angular/forms';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ICertificate } from 'app/shared/model/certificate.model';

@Component({
  selector: 'jhi-signing-office-invisible',
  templateUrl: './signing-office-invisible.component.html',
  styleUrls: ['./signing-office-invisible.component.scss'],
})
export class SigningOfficeInvisibleComponent implements OnInit {
  selectFiles: File[] = [];
  currentFile?: File;
  listCertificate?: ICertificate[];
  authSubscription?: Subscription;
  account: Account | null = null;
  fileName: string | undefined;
  resFile = '';
  disable = false;
  editForm = this.fb.group({
    pinCode: [],
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

  signDoc(): void {
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
          pin: this.editForm.get(['pinCode'])!.value,
          serial: this.editForm.get(['serial'])!.value,
        },
        optional: {
          otpCode: '621143',
        },
      };
      this.signingService.signDocInvisible(request).subscribe((res: any) => {
        this.resFile = JSON.parse(res).data.responseContentList[0].signedDocument;
        const byteArray = this.base64ToArrayBuffer(this.resFile);
        saveAs(
          new Blob([byteArray], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' }),
          Date.now().toString()
        );
      });
    };
  }

  arrayBufferToBase64(buffer: any): string {
    return btoa(new Uint8Array(buffer).reduce((data, byte) => data + String.fromCharCode(byte), ''))
      .toString()
      .replace('data:application/pdf;base64,', '');
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
