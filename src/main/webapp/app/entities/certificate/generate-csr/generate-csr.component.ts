import { Component, OnInit } from '@angular/core';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { saveAs } from 'file-saver';
import { DatePipe } from '@angular/common';
import { ResponseBody } from 'app/shared/model/response-body';

@Component({
  selector: 'jhi-generate-csr',
  templateUrl: './generate-csr.component.html',
})
export class GenerateCsrComponent implements OnInit {
  selectedFiles: any;
  currentFile: any;
  progress = 0;
  fileName: any = this.translate.instant('webappApp.certificate.chooseFile');
  now = new Date();
  currentDay = this.datePipe.transform(this.now, 'dd-MM-yyyy');

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
        saveAs(this.base64toBlob(res.data), 'Certificate-Request-Information-' + this.currentDay + '.xlsx');
        this.toastrService.success(this.translateService.instant('webappApp.certificate.success'));
      } else {
        this.toastrService.error(this.translateService.instant('webappApp.certificate.errorGenerateCsr'));
      }
    });
  }

  base64toBlob(base64Data: string): any {
    const sliceSize = 1024;
    const byteCharacters = atob(base64Data);
    const bytesLength = byteCharacters.length;
    const slicesCount = Math.ceil(bytesLength / sliceSize);
    const byteArrays = new Array(slicesCount);

    for (let sliceIndex = 0; sliceIndex < slicesCount; ++sliceIndex) {
      const begin = sliceIndex * sliceSize;
      const end = Math.min(begin + sliceSize, bytesLength);

      const bytes = new Array(end - begin);
      for (let offset = begin, i = 0; offset < end; ++i, ++offset) {
        bytes[i] = byteCharacters[offset].charCodeAt(0);
      }
      byteArrays[sliceIndex] = new Uint8Array(bytes);
    }
    return new Blob(byteArrays);
  }

  downloadSampleFile(): void {
    this.certificateService.downloadSampleFileCertificate().subscribe((res: ResponseBody) => {
      if (res.status === ResponseBody.SUCCESS) {
        saveAs(this.base64toBlob(res.data), 'Sample-Certificate-Request-Information.xlsx');
        this.toastrService.success(this.translateService.instant('webappApp.certificate.success'));
      } else {
        this.toastrService.error(this.translateService.instant('webappApp.certificate.errorGenerateCsr'));
      }
    });
  }
}
