import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Account } from 'app/core/user/account.model';
import { Subscription } from 'rxjs';
import { AccountService } from 'app/core/auth/account.service';

@Component({
  selector: 'jhi-upload-doc',
  templateUrl: './upload-doc.component.html',
  styleUrls: ['./upload-doc.component.scss'],
})
export class UploadDocComponent implements OnInit {
  @Output() fileOutPut = new EventEmitter<File>();

  selectFiles: File[] = [];
  account: Account | null = null;
  authSubscription?: Subscription;
  fileName: string | undefined;

  constructor(private accountService: AccountService) {}

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
  }

  selectFile(event: any): void {
    if (this.selectFiles.length !== 0) this.removeFile(event);
    this.selectFiles.push(...event.addedFiles);
    this.fileName = this.selectFiles[0].name;
    this.fileOutPut.emit(this.selectFiles[0]);
  }

  removeFile(event: any): void {
    this.selectFiles.splice(this.selectFiles.indexOf(event), 1);
    this.fileOutPut.emit(this.selectFiles[0]);
  }
}
