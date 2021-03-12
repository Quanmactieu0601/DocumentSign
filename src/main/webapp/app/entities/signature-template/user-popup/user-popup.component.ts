import { Component, OnInit } from '@angular/core';
import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Account } from 'app/core/user/account.model';
import { IUser, User } from 'app/core/user/user.model';
import { combineLatest, Subscription } from 'rxjs';
import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { UserService } from 'app/core/user/user.service';
import { AccountService } from 'app/core/auth/account.service';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';
import { JhiAlertService, JhiEventManager } from 'ng-jhipster';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { FormBuilder } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { UserManagementKeyLengthComponent } from 'app/admin/user-management/user-management-key-length.component';
import { UploadUserComponent } from 'app/admin/user-management/upload-user/upload-user-component';
import { Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'jhi-user-popup',
  templateUrl: './user-popup.component.html',
  styleUrls: ['./user-popup.component.scss'],
})
export class UserPopupComponent implements OnInit {
  @Output() userSelectEvent: any = new EventEmitter<User>();

  userSearch = this.fb.group({
    account: [],
    name: [],
    phone: [],
    email: [],
    commonName: [],
    country: [],
  });

  modalRef: NgbModalRef | undefined;

  currentAccount: Account | null = null;
  users: User[] | null = null;
  userListSubscription?: Subscription;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;
  predicate!: string;
  ascending!: boolean;
  listId: number[] = [];
  ngbPaginationPage = 1;

  constructor(
    private userService: UserService,
    private accountService: AccountService,
    private activatedRoute: ActivatedRoute,
    public activeModal: NgbActiveModal,
    private router: Router,
    private eventManager: JhiEventManager,
    private modalService: NgbModal,
    private certificateService: CertificateService,
    private fb: FormBuilder,
    public translateService: TranslateService
  ) {}

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => (this.currentAccount = account));
    this.userListSubscription = this.eventManager.subscribe('userListModification', () => {
      this.loadAll();
    });
    this.handleNavigation();
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }

  searchUser(page?: number): any {
    const data = {
      ...this.userSearch.value,
      page: this.page - 1,
      size: this.itemsPerPage,
      sort: this.sort(),
    };
    const pageToLoad: number = page || this.page || 1;

    if (data.account != null) {
      data.account = data.account.trim();
    }
    if (data.name != null) {
      data.name = data.name.trim();
    }
    if (data.email != null) {
      data.email = data.email.trim();
    }
    if (data.commonName != null) {
      data.commonName = data.commonName.trim();
    }
    if (data.country != null) {
      data.country = data.country.trim();
    }
    if (data.phone != null) {
      data.phone = data.phone.trim();
    }
    data.activated = true;
    this.userService.findByUser(data).subscribe((res: HttpResponse<User[]>) => this.onSuccess(res.body, res.headers));
  }

  transition(): void {
    this.searchUser(this.page);
  }

  private handleNavigation(): void {
    this.page = 1;
    const sort = ['id', 'asc'];
    this.predicate = sort[0];
    this.ascending = sort[1] === 'asc';
    this.searchUser();
  }

  private loadAll(): void {
    this.userService
      .query({
        page: this.page - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe((res: HttpResponse<User[]>) => this.onSuccess(res.body, res.headers));
  }

  private sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  private onSuccess(users: User[] | null, headers: HttpHeaders): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.users = users || [];
  }

  // change select event: add or remove into listId when click each checkbox element
  changeSelect(user: any): void {
    this.cancel(user);
  }

  createCSR(): void {
    const modalRef = this.modalService.open(UserManagementKeyLengthComponent, { size: 'lg', backdrop: 'static' });
    this.userService.setListId(this.listId);
  }

  showUploadComponent(): void {
    this.modalRef = this.modalService.open(UploadUserComponent, { size: 'lg' });
  }

  cancel(user: User): void {
    this.activeModal.close(user);
  }

  cancelEvent(): void {
    this.activeModal.close();
  }
}
