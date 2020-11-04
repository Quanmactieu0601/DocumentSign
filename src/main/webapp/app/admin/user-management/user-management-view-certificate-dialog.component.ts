import { HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'app/core/user/user.model';
import { JhiEventManager } from 'ng-jhipster';
import { CertificateService } from '../../entities/certificate/certificate.service';

@Component({
  selector: 'jhi-view-certificate-dialog',
  templateUrl: './user-management-view-certificate-dialog.component.html',
  providers: [CertificateService],
})
export class UserManagementViewCertificateComponent implements OnInit {
  user?: User;
  certificateList: any;
  constructor(private activeModal: NgbActiveModal, private eventManager: JhiEventManager, private certificateService: CertificateService) {}
  ngOnInit(): void {
    this.getCertificateByCurrentUser();
  }
  close(): void {
    this.activeModal.dismiss();
  }
  getCertificateByCurrentUser(): void {
    this.certificateService.findByCurrentUser(this.user?.id).subscribe((res: HttpResponse<any>) => {
      if (res) {
        this.certificateList = res.body;
      }
    });
  }
}
