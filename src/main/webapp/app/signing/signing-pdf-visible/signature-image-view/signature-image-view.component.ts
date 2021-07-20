import { Component, ElementRef, EventEmitter, OnInit, Output, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { SignatureListComponent } from 'app/signing/signing-pdf-visible/pdf-view/signature-list/signature-list.component';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { AccountService } from 'app/core/auth/account.service';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ResponseBody } from 'app/shared/model/response-body';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'jhi-signature-image-view',
  templateUrl: './signature-image-view.component.html',
  styleUrls: ['./signature-image-view.component.scss'],
})
export class SignatureImageViewComponent implements OnInit {
  @ViewChild('serialElement') serialElement: ElementRef | undefined;
  @ViewChild('signatureImage') signatureImage: ElementRef | undefined;
  @ViewChild('nextBtnElement') nexBtnElement: ElementRef | undefined;
  @Output() nextActionEvent = new EventEmitter<any>();
  isShowMessage = false;
  signingForm = this.fb.group({
    serial: ['', Validators.required],
    pin: ['', Validators.required],
    templateId: ['', Validators.required],
  });

  showMessageSerialRequired = false;
  modalRef: NgbModalRef | undefined;

  constructor(
    private fb: FormBuilder,
    private el: ElementRef,
    private modalService: NgbModal,
    protected accountService: AccountService,
    private certificateService: CertificateService,
    private toastrService: ToastrService,
    private translateService: TranslateService
  ) {}

  ngOnInit(): void {}

  openModalTemplateList(): void {
    this.modalRef = this.modalService.open(SignatureListComponent, { size: 'md' });
    this.modalRef.result.then(templateId => {
      this.signingForm.controls['templateId'].setValue(templateId);
    });
    this.accountService.identity(false).subscribe(res => {
      this.modalRef!.componentInstance.userId = res?.id;
    });
  }

  checkValidatedImage(): void {
    for (const key of Object.keys(this.signingForm.controls)) {
      if (this.signingForm.controls[key].invalid) {
        const invalidControl = this.el.nativeElement.querySelector('[formcontrolname="' + key + '"]');
        invalidControl.focus();
        this.isShowMessage = true;
        return;
      }
    }

    const data = {
      ...this.signingForm.value,
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
    this.nextActionEvent?.emit({ signatureImage: this.signatureImage?.nativeElement.src, signingForm: this.signingForm });
  }

  setDisableButton(): void {
    this.nexBtnElement!.nativeElement.disabled = true;
  }
}
