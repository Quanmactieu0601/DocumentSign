import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Observable } from 'rxjs';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ResponseBody } from 'app/shared/model/response-body';

@Component({
  selector: 'jhi-upload-certificate',
  templateUrl: './upload-certificate.component.html',
  styleUrls: ['./upload-certificate.component.scss'],
})
export class UploadCertificateComponent implements OnInit {
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
    const sizeFile = event.target.files.item(0).size / 10240000;
    if (sizeFile > 1) {
      this.toastService.error('Dung lượng tệp tải lên phải nhỏ hơn 10MB');
      this.selectedFiles = [];
    }
    this.fileName = this.selectedFiles[0].name;
  }

  upload(): void {
    this.progress = 0;
    this.currentFile = this.selectedFiles.item(0);
    this.certificateService.importCertToHsm(this.currentFile).subscribe((res: ResponseBody) => {
      if (res.status === ResponseBody.SUCCESS) {
        this.toastService.success(this.translate.instant('webappApp.certificate.uploadCert.alert.success'));
        this.transformVariable(true);
        this.activeModal.dismiss();
      } else {
        this.toastService.error(this.translate.instant('webappApp.certificate.uploadCert.alert.error', { message: res.msg }));
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
