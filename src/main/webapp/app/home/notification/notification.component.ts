import { Component, OnInit } from '@angular/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { HomeLayoutComponent } from 'app/layouts/home-layout/home-layout.component';
import { LocalStorageService } from 'ngx-webstorage';

@Component({
  selector: 'jhi-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.scss'],
})
export class NotificationComponent implements OnInit {
  checkNo = 'show';
  constructor(public activeModal: NgbActiveModal, private modalService: NgbModal, private $localStorage: LocalStorageService) {}

  ngOnInit(): void {}

  cancel(): void {
    this.activeModal.dismiss();
  }
  markAsRead(): void {
    this.activeModal.dismiss();
    this.$localStorage.store('key', 'value');
  }
}
