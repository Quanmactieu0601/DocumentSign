import { Component, ElementRef, EventEmitter, HostListener, OnInit, Output, ViewChild } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { ICertificate } from 'app/shared/model/certificate.model';
import { Subscription } from 'rxjs';
import { Account } from 'app/core/user/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { SigningService } from 'app/signing/signing.service';
import { FormBuilder, Validators } from '@angular/forms';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { HttpResponse } from '@angular/common/http';
import { saveAs } from 'file-saver';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { SignatureListComponent } from 'app/signing/pdf-view/signature-list/signature-list.component';
import { ResponseBody } from 'app/shared/model/response-body';

@Component({
  selector: 'jhi-signing',
  templateUrl: './signing.component.html',
  styleUrls: ['./signing.component.scss'],
})
export class SigningComponent implements OnInit {
  @ViewChild('serialElement') serialElement: ElementRef | undefined;
  @ViewChild('signatureImage') signatureImage: ElementRef | undefined;
  @ViewChild('nextBtnElement') nexBtnElement: ElementRef | undefined;
  @ViewChild('wizzard') wizzard: any;
  @Output() nextActionEvent = new EventEmitter<any>();
  isShowMessage = false;
  FileToSign: any = null;
  srcPdfResult: any;
  imageSrc: any;
  serial = '';
  pin: any;

  showMessageSerialRequired = false;
  modalRef: NgbModalRef | undefined;

  selectFiles: File[] = [];
  currentFile?: File;
  listCertificate: ICertificate[] = [];
  authSubscription?: Subscription;
  account: Account | null = null;
  fileName: string | undefined;
  resFile = '';
  page = 0;
  timer: NodeJS.Timeout | undefined;
  editForm = this.fb.group({
    serial: ['', Validators.required],
    pin: ['', Validators.required],
    templateId: [''],
    fileSelect: ['', Validators.required],
  });
  constructor(
    private accountService: AccountService,
    private toastrService: ToastrService,
    private translateService: TranslateService,
    private signingService: SigningService,
    private fb: FormBuilder,
    private certificateService: CertificateService,
    private el: ElementRef,
    private modalService: NgbModal
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
    this.serial = serial;
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
    this.editForm.controls['fileSelect'].setValue(1);
    this.editForm.controls['fileSelect'].markAllAsTouched();
    this.validateFileInput(event.addedFiles[0]);
  }

  removeFile(event: any): void {
    this.selectFiles.splice(this.selectFiles.indexOf(event), 1);
    this.editForm.controls['fileSelect'].setValue('');
    this.setDisableButton();
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

  openModalTemplateList(): void {
    this.modalRef = this.modalService.open(SignatureListComponent, { size: 'md' });
    this.modalRef.result.then(templateId => {
      this.editForm.controls['templateId'].setValue(templateId);
    });
    this.accountService.identity(false).subscribe(res => {
      this.modalRef!.componentInstance.userId = res?.id;
    });
  }

  checkValidatedImage(): void {
    for (const key of Object.keys(this.editForm.controls)) {
      if (this.editForm.controls[key].invalid) {
        const invalidControl = this.el.nativeElement.querySelector('[formcontrolname="' + key + '"]');
        invalidControl.focus();
        this.isShowMessage = true;
        return;
      }
    }

    const data = {
      ...this.editForm.value,
    };

    this.certificateService.getSignatureImageByTemplateId(data).subscribe((res: ResponseBody) => {
      if (res.status === ResponseBody.SUCCESS) {
        this.toastrService.success(this.translateService.instant('sign.messages.validate.validated'));
        this.signatureImage && this.signatureImage.nativeElement
          ? (this.signatureImage.nativeElement.src = 'data:image/jpeg;base64,' + res.data)
          : null;
        this.nexBtnElement!.nativeElement.disabled = false;
        this.isShowMessage = true;
      } else {
        this.toastrService.error(res.msg);
      }
    });
  }

  nextAction(): void {
    this.imageSrc = this.signatureImage?.nativeElement.src;
    this.wizzard.goToNextStep();
  }

  validateFileInput(FileToSign: File): any {
    if (typeof FileReader !== 'undefined') {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.FileToSign = e.target.result;
      };

      if (FileToSign) reader.readAsArrayBuffer(FileToSign);
      else this.FileToSign = FileToSign;
    }
  }

  cancel(): void {
    this.wizzard.goToPreviousStep();
  }

  signResult(signedFile: any): void {
    if (signedFile) {
      this.wizzard.goToNextStep();
      this.srcPdfResult = signedFile;
    }
  }

  setDisableButton(): void {
    this.nexBtnElement!.nativeElement.disabled = true;
  }
}
