import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ResponseBody } from 'app/shared/model/response-body';
import { saveAs } from 'file-saver';
import { FileDataUtil } from 'app/shared/util/file-data.util';

@Component({
  selector: 'jhi-upload-excel-register',
  templateUrl: './upload-excel-register.component.html',
  styleUrls: ['./upload-excel-register.component.scss'],
})
export class UploadExcelRegisterComponent implements OnInit {
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
    this.fileName = this.selectedFiles[0].name;
  }

  upload(): void {
    this.progress = 0;

    this.currentFile = this.selectedFiles;

    this.certificateService.uploadExcelRegisterFile(this.currentFile).subscribe((response: ResponseBody) => {
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
