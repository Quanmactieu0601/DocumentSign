import { Component, OnInit } from '@angular/core';
import { Account } from 'app/core/user/account.model';
import { Subscription } from 'rxjs';
import { AccountService } from 'app/core/auth/account.service';
import { ToastrService } from 'ngx-toastr';
import { VerifySignatureService } from 'app/verify/verify-signature.service';
import { ISignatureVfVM, SignatureVfVM } from 'app/shared/model/signatureVfVM.model';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ElementVM, IElementVM } from 'app/shared/model/elementVM.model';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ICertificate } from 'app/shared/model/certificate.model';

@Component({
  selector: 'jhi-verify-signature',
  templateUrl: './verify-signature-hash.component.html',
  styleUrls: [],
})
export class VerifySignatureHashComponent implements OnInit {
  progress = 0;
  account: Account | null = null;
  authSubscription?: Subscription;
  certificate: any;
  signatureVfVM: ISignatureVfVM | undefined;
  elementVM: IElementVM | undefined;
  result = false;
  bar = false;
  listCertificate?: ICertificate[];
  editForm = this.fb.group({
    base64Signature: [],
    base64OriginalData: [],
    serial: [],
  });
  constructor(
    private accountService: AccountService,
    private verifySignatureService: VerifySignatureService,
    private toastrService: ToastrService,
    private certificateService: CertificateService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
    this.getListCertificate();
  }

  getListCertificate(): void {
    this.certificateService.query().subscribe((res: HttpResponse<ICertificate[]>) => (this.listCertificate = res.body || []));
  }

  private createFromForm(): ISignatureVfVM {
    return {
      ...new SignatureVfVM(),
      elements: [
        {
          ...new ElementVM(),
          base64Signature: this.editForm.get(['base64Signature'])!.value,
          base64OriginalData: btoa(this.editForm.get(['base64OriginalData'])!.value),
          key: '123',
        },
      ],
      hashAlgorithm: 'SHA1',
      serial: this.editForm.get(['serial'])!.value,
    };
  }

  // selectFile(event: any): void {
  //   this.selectFiles = event.target.files;
  //   this.fileName = event.target.files[0].name;
  // }

  verifyHash(): void {
    this.progress = 0;
    this.signatureVfVM = this.createFromForm();
    if (this.account != null) {
      this.verifySignatureService.verifyHash(this.signatureVfVM).subscribe((res: any) => {
        if (res.type === HttpEventType.UploadProgress) {
          this.bar = true;
          this.progress = Math.round((100 * res.loaded) / res.total);
        } else if (res instanceof HttpResponse) {
          console.error(res.body.data);
          if (res.body.data == null) {
            this.result = false;
            this.certificate = null;
            this.toastrService.error('False');
          }
          if (res.status === 200) {
            this.certificate = res.body.data.certificate;
            this.result = res.body.data.elements[0].result;
            if (!this.result) this.toastrService.error('False');
          } else {
            this.toastrService.error('Error');
          }
        }
      });
    }
  }
}
