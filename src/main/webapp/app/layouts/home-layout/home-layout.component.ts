import { AfterViewInit, Component, OnInit } from '@angular/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NotificationComponent } from 'app/home/notification/notification.component';
import { LocalStorageService } from 'ngx-webstorage';
import { getLocalStorage } from 'ngx-webstorage/lib/core/nativeStorage';

@Component({
  selector: 'jhi-home-layout',
  templateUrl: './home-layout.component.html',
  styleUrls: ['./home-layout.component.scss'],
})
export class HomeLayoutComponent implements OnInit, AfterViewInit {
  notifyKey = 'linh';

  constructor(private modalService: NgbModal, private $localStorage: LocalStorageService, public activeModal: NgbActiveModal) {}

  ngOnInit(): void {}

  ngAfterViewInit(): void {
    const notifyStorage = this.$localStorage.retrieve('key');
    if (notifyStorage === null) {
      this.modalService.open(NotificationComponent, { size: 'lg', backdrop: 'static' });
    }
  }
}
