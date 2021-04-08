import { Component, OnInit } from '@angular/core';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { saveAs } from 'file-saver';
import { DatePipe } from '@angular/common';
import { ResponseBody } from 'app/shared/model/response-body';
import { FileDataUtil } from 'app/shared/util/file-data.util';

@Component({
  selector: 'jhi-generate-csr',
  templateUrl: './generate-csr.component.html',
})
export class GenerateCsrComponent implements OnInit {
  selectedFiles: any;
  currentFile: any;
  progress = 0;
  fileName: any = this.translate.instant('webappApp.certificate.chooseFile');

  constructor(
    private certificateService: CertificateService,
    private toastService: ToastrService,
    private translate: TranslateService,
    public activeModal: NgbActiveModal,
    private translateService: TranslateService,
    private toastrService: ToastrService,
    private datePipe: DatePipe
  ) {}

  ngOnInit(): void {}

  selectFile(event: any): void {
    this.selectedFiles = event.target.files;
    this.fileName = this.selectedFiles[0].name;
  }

  genCsrOfCertificate(): void {
    this.currentFile = this.selectedFiles;
    this.certificateService.generateCertificateRequestInformation(this.currentFile).subscribe((res: ResponseBody) => {
      if (res.status === ResponseBody.SUCCESS) {
        const currentDay = this.datePipe.transform(new Date(), 'yyyyMMdd');
        saveAs(FileDataUtil.base64toBlob(res.data), 'Certificate-Request-Information-' + currentDay + '.xlsx');
        this.toastrService.success(this.translateService.instant('webappApp.certificate.generateCSR.alertGenerateCsr.success'));
        this.activeModal.dismiss();
      } else {
        this.toastrService.error(this.translateService.instant('webappApp.certificate.generateCSR.alertGenerateCsr.error'));
      }
    });
  }

  downloadSampleFile(): void {
    this.certificateService.downloadSampleFileCertificate().subscribe((res: ResponseBody) => {
      if (res.status === ResponseBody.SUCCESS) {
        saveAs(FileDataUtil.base64toBlob(res.data), 'Sample-Certificate-Request-Information.xlsx');
        this.toastrService.success(this.translateService.instant('webappApp.certificate.generateCSR.alertDownSampleFile.success'));
      } else {
        this.toastrService.error(this.translateService.instant('webappApp.certificate.generateCSR.alertDownSampleFile.error'));
      }
    });
  }
}
