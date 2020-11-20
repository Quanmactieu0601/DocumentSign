import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { UserService } from 'app/core/user/user.service';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { JhiEventManager } from 'ng-jhipster';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
@Component({
  selector: 'jhi-user-mgmt-kl',
  templateUrl: './user-management-key-length.component.html',
})
export class UserManagementKeyLengthComponent {
  keyLength: any;
  listId: number[] = [];
  radioItems = [1024, 2048, 4096];
  model = { options: 1024 };
  constructor(
    private userService: UserService,
    public activeModal: NgbActiveModal,
    private eventManager: JhiEventManager,
    private certificateService: CertificateService,
    private toastrService: ToastrService,
    private translateService: TranslateService
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  saveAndGenCSR(): void {
    this.listId = this.userService.getListId();

    if (this.listId.length > 0) {
      this.certificateService
        .sendData({
          userIds: this.listId,
          keyLen: this.model.options,
        })
        .subscribe((response: any) => {
          if (response.byteLength === 0) {
            this.toastrService.error(this.translateService.instant('userManagement.alert.fail.csrExported'));
          } else {
            this.toastrService.success(this.translateService.instant('userManagement.alert.success.csrExported'));
            saveAs(new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }), 'excel.xlsx');
          }
        });
      console.error(this.listId, this.keyLength);
    }
  }
}
