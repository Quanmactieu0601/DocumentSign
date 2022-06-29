import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { saveAs } from 'file-saver';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ResponseBody } from 'app/shared/model/response-body';
import { FileDataUtil } from 'app/shared/util/file-data.util';
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
  fileName: any = this.translate.instant('webappApp.certificate.chooseFile');
  constructor(
    private certificateService: CertificateService,
    private toastService: ToastrService,
    private translate: TranslateService,
    private toastrService: ToastrService,
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

    this.certificateService.uploadP12(this.currentFile).subscribe((response: ResponseBody) => {
      // if (response.type === 0) {
      //   this.progress = 100;
      // } else {
      //   saveAs(new Blob([response.body], { type: 'application/zip' }), 'SuccessAndError.Zip');
      //   this.transformVariable(true);
      // }

      // if (response.byteLength === 0) {
      //   this.toastrService.error(this.translate.instant('userManagement.alert.fail.csrExported'));
      // } else {
      //   const filename: string = 'EasyCA-CSR-Export-' + new Date() + '.xlsx';
      //   saveAs(new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }), filename);
      //   this.toastrService.success(this.translate.instant('userManagement.alert.success.csrExported'));
      // }

      if (response.status === ResponseBody.SUCCESS) {
        const filename: string = 'EasyCA-CSR-Export-' + new Date() + '.xlsx';
        saveAs(
          new Blob([FileDataUtil.base64ToArrayBuffer(response.data)], {
            type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
          }),
          filename
        );
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
