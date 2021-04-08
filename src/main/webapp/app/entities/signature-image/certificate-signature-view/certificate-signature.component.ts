import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ICertificate } from 'app/shared/model/certificate.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DomSanitizer } from '@angular/platform-browser';
import { SignatureImageService } from 'app/entities/signature-image/signature-image.service';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'jhi-imagesign',
  templateUrl: './certificate-signature.component.html',
  styleUrls: ['../signature-image.component.scss'],
})
export class CertificateSignatureComponent implements OnInit {
  @ViewChild('imageUpload') imageUpload: ElementRef | undefined;

  certificate?: ICertificate;
  image?: any;
  fileName: any = this.translateService.instant('webappApp.signatureImage.showImageSign.chooseImage');
  imageFiles: any;
  results: any | null;
  imageUrl: any;

  constructor(
    public activeModal: NgbActiveModal,
    private translateService: TranslateService,
    private sanitizer: DomSanitizer,
    private signatureImageService: SignatureImageService,
    private toastrService: ToastrService
  ) {}

  getImage(id: number | undefined): any {
    if (id === null) {
      this.toastrService.error(this.translateService.instant('webappApp.signatureImage.showImageSign.showAlert'));
      this.imageUpload?.nativeElement?.addAttribute('hidden');
    } else {
      this.signatureImageService.getBase64(id).subscribe((res: any) => {
        this.imageUpload?.nativeElement?.removeAttribute('hidden');
        this.image = this.sanitizer.bypassSecurityTrustResourceUrl('data:image/png;base64, ' + res.body['data']);
      });
    }
  }

  ngOnInit(): void {
    this.getImage(this.certificate?.signatureImageId);
  }

  choose(_event: any): any {
    this.imageFiles = _event.target.files;
    const sizeFile = _event.target.files.item(0).size / 102400;
    if (sizeFile > 1) {
      this.toastrService.error(this.translateService.instant('webappApp.signatureImage.showImageSign.alert'));
      this.imageFiles = [];
    } else {
      const reader = new FileReader();
      reader.onload = (event: any) => {
        this.image = event.target.result;
      };
      this.imageUpload?.nativeElement?.removeAttribute('hidden');
      reader.readAsDataURL(this.imageFiles[0]);
      this.fileName = this.imageFiles[0].name;
    }
  }

  save(): any {
    this.signatureImageService.saveImage(this.imageFiles, this.certificate?.id).subscribe((res: any) => {
      this.results = res.body['data'];
      this.toastrService.success(this.translateService.instant('webappApp.signatureImage.showImageSign.success'));
      this.activeModal.close(this.results);
    });
  }

  cancel(): void {
    this.activeModal.close();
  }
}
