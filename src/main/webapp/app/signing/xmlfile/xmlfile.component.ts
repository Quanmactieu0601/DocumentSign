import { Component, ElementRef, HostListener, OnInit } from '@angular/core';
import { AccountService } from 'app/core/auth/account.service';
import { Subscription } from 'rxjs';
import { Account } from 'app/core/user/account.model';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { HttpResponse } from '@angular/common/http';
import { saveAs } from 'file-saver';
import { FormBuilder, Validators } from '@angular/forms';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ICertificate } from 'app/shared/model/certificate.model';
import { SigningService } from 'app/core/signing/signing.service';
import { error } from '@angular/compiler/src/util';
import { ResponseBody } from 'app/shared/model/response-body';
import { FileDataUtil } from 'app/shared/util/file-data.util';
@Component({
  selector: 'jhi-xmlfile',
  templateUrl: './xmlfile.component.html',
  styleUrls: ['./xmlfile.component.scss'],
})
export class XmlfileComponent implements OnInit {
  selectFiles: File[] = [];
  currentFile?: File;
  listCertificate: ICertificate[] = [];
  filterCertificate: ICertificate[] = [];
  authSubscription?: Subscription;
  account: Account | null = null;
  fileName: string | undefined;
  resFile = '';
  serial = '';
  page = 0;
  timer: NodeJS.Timeout | undefined;
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
    const data = {
      page: 0,
      size: 100,
      sort: ['id,desc'],
      alias: null,
      ownerId: this.account?.login,
      serial: null,
      validDate: null,
      expiredDate: null,
    };

    this.certificateService.findCertificate(data).subscribe((res: HttpResponse<ICertificate[]>) => {
      this.listCertificate = res.body || [];
      this.filterCertificate = this.listCertificate;
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
          serial: this.serial,
        },
        optional: {
          otpCode: '621143',
        },
      };
      this.signingService.signXml(request).subscribe(
        (res: any) => {
          if (JSON.parse(res).status === -1) this.toastrService.error(JSON.parse(res).msg);
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
        () => this.toastrService.error(this.translateService.instant('sign.messages.signingFail'))
      );
    };
  }

  downLoadFileTemplate(): void {
    this.signingService.downLoadTemplateFile().subscribe((res: ResponseBody) => {
      if (res.status === ResponseBody.SUCCESS) {
        saveAs(FileDataUtil.base64toBlob(res.data), 'XML-Template-File.xml');
        this.toastrService.success(this.translateService.instant('userManagement.downloadSampleFileUser.success'));
      } else {
        this.toastrService.error(this.translateService.instant('userManagement.downloadSampleFileUser.error'));
      }
    });
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

  selectSerial(serial: string): void {
    this.serial = serial;
  }

  filter(part: string): void {
    this.filterCertificate = this.listCertificate.filter(item => {
      return item.serial?.includes(part);
    });
  }
}
