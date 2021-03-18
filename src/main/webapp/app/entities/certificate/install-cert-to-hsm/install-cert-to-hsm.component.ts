import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ResponseBody } from 'app/shared/model/response-body';
import { saveAs } from 'file-saver';
import { FileDataUtil } from 'app/shared/util/file-data.util';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'jhi-install-cert-to-hsm',
  templateUrl: './install-cert-to-hsm.component.html',
})
export class InstallCertToHsmComponent implements OnInit {
  @Output() isUploadedSucessfully = new EventEmitter<boolean>();
  selectedFiles: any;
  currentFile: any;
  progress = 0;
  fileName: any = this.translate.instant('webappApp.certificate.chooseFile');

  constructor(
    private certificateService: CertificateService,
    private toastService: ToastrService,
    private translate: TranslateService,
    public activeModal: NgbActiveModal,
    private datePipe: DatePipe
  ) {}

  ngOnInit(): void {}

  selectFile(event: any): void {
    this.selectedFiles = event.target.files;
    const sizeFile = event.target.files.item(0).size / 10240000;
    if (sizeFile > 1) {
      this.toastService.error(this.translate.instant('webappApp.certificate.installCertIntoHsm.notification'));
      this.selectedFiles = [];
    }
    this.fileName = this.selectedFiles[0].name;
  }

  upload(): void {
    this.progress = 0;
    this.currentFile = this.selectedFiles.item(0);
    this.certificateService.importCertToHsm(this.currentFile).subscribe((res: ResponseBody) => {
      if (res.status === ResponseBody.SUCCESS) {
        const currentDay = this.datePipe.transform(new Date(), 'yyyyMMdd');
        saveAs(FileDataUtil.base64toBlob(res.data), 'HSM_Serial_PIN_Result-' + currentDay + '.xlsx');
        this.toastService.success(this.translate.instant('webappApp.certificate.installCertIntoHsm.alert.success'));
        this.activeModal.dismiss();
      } else {
        this.toastService.error(this.translate.instant('webappApp.certificate.installCertIntoHsm.alert.error', { message: res.msg }));
      }
    });
  }
}
