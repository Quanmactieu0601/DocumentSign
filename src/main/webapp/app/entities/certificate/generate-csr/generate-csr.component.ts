import { Component, OnInit } from '@angular/core';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
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
  upload(): void {
    this.currentFile = this.selectedFiles;
    this.certificateService.uploadFileCSR(this.currentFile).subscribe();
    this.toastrService.success(this.translateService.instant('webappApp.certificate.success'));
  }

  downloadSampleFile(): void {
    this.certificateService.sampleFile().subscribe((res: any) => {
      const bindData = [];
      bindData.push(res.body);
      const url = window.URL.createObjectURL(
        new Blob(bindData, { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
      );
      const templateFile = document.createElement('a');
      templateFile.href = url;
      templateFile.download = 'Certificate-Request-Information';
      templateFile.click();
      this.toastrService.success(this.translateService.instant('webappApp.certificate.success'));
    });
  }
}
