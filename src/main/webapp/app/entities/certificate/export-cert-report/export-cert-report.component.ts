import { Component, OnInit } from '@angular/core';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DatePipe } from '@angular/common';
import { ResponseBody } from 'app/shared/model/response-body';
import { saveAs } from 'file-saver';
import { FileDataUtil } from 'app/shared/util/file-data.util';

@Component({
  selector: 'jhi-export-cert-report',
  templateUrl: './export-cert-report.component.html',
  styleUrls: ['./export-cert-report.component.scss'],
})
export class ExportCertReportComponent implements OnInit {
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

  downloadCertReport(): void {
    this.certificateService.downloadSigningTurnCountCertReport().subscribe((res: ResponseBody) => {
      if (res.status === ResponseBody.SUCCESS) {
        saveAs(FileDataUtil.base64toBlob(res.data), 'EasySign-Cert-Report.xlsx');
        this.toastrService.success(this.translateService.instant('webappApp.certificate.getSigningTurnCountReport.alertDownFile.success'));
      } else {
        this.toastrService.error(this.translateService.instant('webappApp.certificate.getSigningTurnCountReport.alertDownFile.error'));
      }
    });
  }
}
