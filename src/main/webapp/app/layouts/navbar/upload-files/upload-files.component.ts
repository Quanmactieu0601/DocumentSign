import { Component, OnInit } from '@angular/core';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserService } from 'app/core/user/user.service';
import { CertificateService } from 'app/entities/certificate/certificate.service';
@Component({
  selector: 'jhi-upload-files',
  templateUrl: './upload-files.component.html',
  styleUrls: ['./upload-files.component.scss'],
})
export class UploadFilesComponent implements OnInit {
  selectedFiles: any;
  currentFile: any;
  progress = 0;
  message = '';

  fileInfos: Observable<any> = new Observable<any>();
  constructor(private certificateService: CertificateService) {}

  ngOnInit(): void {}
  selectFile(event: any): void {
    this.selectedFiles = event.target.files;
  }
  upload(): void {
    this.progress = 0;

    this.currentFile = this.selectedFiles.item(0);
    this.certificateService.upload(this.currentFile).subscribe((res: any) => {
      if (res.type === HttpEventType.UploadProgress) {
        this.progress = Math.round((100 * res.loaded) / res.total);
      } else if (res instanceof HttpResponse) {
        this.message = res.body.message;
        this.fileInfos = this.certificateService.getFiles();
      }
    });
    this.selectedFiles = [];
  }
}
