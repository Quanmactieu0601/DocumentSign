import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ResponseBody } from 'app/shared/model/response-body';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { ICertificate } from 'app/shared/model/certificate.model';

@Component({
  selector: 'jhi-otp',
  templateUrl: './otp.component.html',
  styleUrls: ['./otp.component.scss'],
})
export class OtpComponent implements OnInit {
  certificate?: ICertificate;
  pin = '';
  base64Img: SafeResourceUrl | null = null;

  constructor(
    public activeModal: NgbActiveModal,
    private eventManager: JhiEventManager,
    private certificateService: CertificateService,
    private _sanitizer: DomSanitizer,
    private toastrService: ToastrService,
    private translateService: TranslateService
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  ngOnInit(): void {}

  genOtp(): void {
    const data = {
      serial: this.certificate?.serial,
      pin: this.pin,
    };
    this.certificateService.getQRCodeOTP(data).subscribe((res: ResponseBody) => {
      if (res.status === ResponseBody.SUCCESS) {
        this.toastrService.success(this.translateService.instant('webappApp.certificate.otp.success'));
        this.base64Img = this._sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + res.data);
      } else {
        this.toastrService.error(res.msg);
        this.toastrService.error(this.translateService.instant('webappApp.certificate.otp.error'));
      }
    });
  }

  showGuide(): void {
    this.toastrService.warning('Chức năng đang phát triển...');
  }
}
