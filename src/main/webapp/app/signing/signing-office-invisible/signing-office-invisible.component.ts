import { Component, HostListener, OnInit } from '@angular/core';
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
import { FileDataUtil } from 'app/shared/util/file-data.util';

@Component({
  selector: 'jhi-signing-office-invisible',
  templateUrl: './signing-office-invisible.component.html',
  styleUrls: ['./signing-office-invisible.component.scss'],
})
export class SigningOfficeInvisibleComponent implements OnInit {
  selectFiles: File[] = [];
  currentFile?: File;
  listCertificate: ICertificate[] = [];
  authSubscription?: Subscription;
  account: Account | null = null;
  fileName: string | undefined;
  signType: string | undefined;
  resFile = '';
  serial = '';
  page = 0;
  timer: NodeJS.Timeout | undefined;
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
    this.getListCertificate('', 0);
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

  selectFile(event: any): void {
    if (this.selectFiles.length !== 0) this.removeFile(event);
    this.selectFiles.push(...event.addedFiles);
    this.fileName = this.selectFiles[0].name;
    if (this.fileName.toString().endsWith('.docx') || this.fileName.toString().endsWith('.doc')) {
      this.signType = 'word';
    } else if (this.fileName.toString().endsWith('.xlsx') || this.fileName.toString().endsWith('.xls')) {
      this.signType = 'excel';
    }
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
          // serial: this.editForm.get(['serial'])!.value,
          serial: this.editForm.get(['serial'])!.value,
        },
        optional: {
          otpCode: '621143',
        },
      };
      this.signingService.signDocInvisible(request).subscribe((res: any) => {
        if (JSON.parse(res).status === -1) this.toastrService.error(JSON.parse(res).msg);
        this.resFile = JSON.parse(res).data.responseContentList[0].signedDocument;
        const byteArray = FileDataUtil.base64ToArrayBuffer(this.resFile);
        if (this.signType === 'word') {
          saveAs(
            new Blob([byteArray], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' }),
            Date.now().toString()
          );
        } else {
          saveAs(
            new Blob([byteArray], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }),
            Date.now().toString()
          );
        }
      });
    };
  }

  arrayBufferToBase64(buffer: any): string {
    return btoa(new Uint8Array(buffer).reduce((data, byte) => data + String.fromCharCode(byte), ''))
      .toString()
      .replace('data:application/pdf;base64,', '');
  }
}
