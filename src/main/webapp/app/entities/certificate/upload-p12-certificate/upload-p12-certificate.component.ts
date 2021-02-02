import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Observable } from 'rxjs';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { saveAs } from 'file-saver';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
@Component({
  selector: 'jhi-upload-p12-certificate',
  templateUrl: './upload-p12-certificate.component.html',
  styleUrls: ['./upload-p12-certificate.component.scss'],
})
export class UploadP12CertificateComponent implements OnInit {
  @Output() isUploadedSucessfully = new EventEmitter<boolean>();
  selectedFiles: any;
  currentFile: any;
  progress = 0;
  fileInfos: Observable<any> = new Observable<any>();
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
    this.progress = 0;

    this.currentFile = this.selectedFiles;

    this.certificateService.uploadP12(this.currentFile).subscribe((response: any) => {
      if (response.type === 0) {
        this.progress = 100;
      } else {
        saveAs(new Blob([response.body], { type: 'application/zip' }), 'SuccessAndError.Zip');
        this.transformVariable(true);
      }
    });
  }

  transformVariable(agreed: boolean): void {
    this.isUploadedSucessfully.emit(agreed);
  }
  onInputClick = (event: any) => {
    const element = event.target as HTMLInputElement;
    element.value = '';
  };
}
