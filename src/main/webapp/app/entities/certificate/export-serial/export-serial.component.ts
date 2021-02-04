import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { saveAs } from 'file-saver';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
@Component({
  selector: 'jhi-upload-p12-certificate',
  templateUrl: './export-serial.component.html',
})
export class ExportSerialComponent implements OnInit {
  @Output() isUploadedSucessfully = new EventEmitter<boolean>();
  selectedFiles: any;
  currentFile: any;
  progress = 0;
  // fileInfos: Observable<any> = new Observable<any>();
  fileName: any = this.translate.instant('webappApp.certificate.chooseFile');
  constructor(
    private certificateService: CertificateService,
    private toastService: ToastrService,
    private translate: TranslateService,
    public activeModal: NgbActiveModal
  ) {}

  ngOnInit(): void {}
  selectFile(event: any): void {
    this.selectedFiles = event.target.files;
    const sizeFile = event.target.files.item(0).size / 1024000;
    if (sizeFile > 1) {
      this.toastService.error('Dung lượng tệp tải lên phải nhỏ hơn 1MB');
      this.selectedFiles = [];
    }
    this.fileName = this.selectedFiles[0].name;
  }
  upload(): void {
    this.currentFile = this.selectedFiles;

    this.certificateService.exportSerial(this.currentFile).subscribe((response: any) => {
      if (response.type !== 0) {
        saveAs(new Blob([response.body]), 'ExportSerial.csv');
        this.toastService.success(this.translate.instant('webappApp.certificate.success'));
        this.activeModal.close();
      }
    });
  }

  onInputClick = (event: any) => {
    const element = event.target as HTMLInputElement;
    element.value = '';
  };
}
