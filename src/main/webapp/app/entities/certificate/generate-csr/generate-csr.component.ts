import { Component, OnInit } from '@angular/core';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { saveAs } from 'file-saver';
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
    private toastrService: ToastrService
  ) {}

  ngOnInit(): void {}
  selectFile(event: any): void {
    this.selectedFiles = event.target.files;
    this.fileName = this.selectedFiles[0].name;
  }
  genCsrOfCertificate(): void {
    this.currentFile = this.selectedFiles;
    this.certificateService.generateCertificateRequestInformation(this.currentFile).subscribe((res: any) => {
      // if (res.body['status'] === -1) {
      //   this.toastrService.error(this.translateService.instant('webappApp.certificate.errorGenerateCsr'));
      // } else {
      saveAs(new Blob([res.body]), 'Certificate-Request-Information.xlsx');
      this.toastrService.success(this.translateService.instant('webappApp.certificate.success'));
      // }
    });
  }

  downloadSampleFile(): void {
    this.certificateService.downloadSampleFileCertificate().subscribe((res: any) => {
      saveAs(new Blob([res.body]), 'Sample-Certificate-Request-Information.xlsx');
      this.toastrService.success(this.translateService.instant('webappApp.certificate.success'));
    });
  }
}
