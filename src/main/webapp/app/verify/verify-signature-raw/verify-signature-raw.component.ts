import {Component, OnInit} from '@angular/core';
import {Account} from 'app/core/user/account.model';
import {Subscription} from 'rxjs';
import {AccountService} from 'app/core/auth/account.service';
import {ToastrService} from 'ngx-toastr';
import {HttpEventType, HttpResponse} from '@angular/common/http';
import {VerifySignatureRawService} from 'app/verify/verify-signature-raw/verify-signature-raw.service';
import {ISignatureVfDTO} from 'app/shared/model/signatureVfDTO.model';

@Component({
  selector: 'jhi-verify-signature',
  templateUrl: './verify-signature-raw.component.html',
  styleUrls: ['./verify-signature-raw.component.scss'],
})
export class VerifySignatureRawComponent implements OnInit {
  selectFiles: any;
  progress = 0;
  currentFile: any;
  account: Account | null = null;
  authSubscription?: Subscription;
  fileName: string | undefined;
  signatureVfDTOs?: ISignatureVfDTO[];

  constructor(
    private accountService: AccountService,
    private verifySignatureService: VerifySignatureRawService,
    private toastrService: ToastrService
  ) {
  }

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
  }

  selectFile(event: any): void {
    this.selectFiles = event.target.files;
    this.fileName = event.target.files[0].name;
  }



  // changeGender(event: any): void {
  //   this.typeVf = event.target.value;
  // }
}
