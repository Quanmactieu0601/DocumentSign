import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse, HttpHeaders } from '@angular/common/http';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Subscription, combineLatest } from 'rxjs';
import { ActivatedRoute, ParamMap, Router, Data } from '@angular/router';
import { JhiAlertService, JhiEventManager } from 'ng-jhipster';
import { FormBuilder } from '@angular/forms';
import { saveAs } from 'file-saver';
import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { UserService } from 'app/core/user/user.service';
import { UserManagementDeleteDialogComponent } from './user-management-delete-dialog.component';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { UserManagementViewCertificateComponent } from './user-management-view-certificate-dialog.component';
import { map } from 'rxjs/operators';
import { IUser, User } from '../../core/user/user.model';
import { UserManagementKeyLengthComponent } from './user-management-key-length.component';

@Component({
  selector: 'jhi-user-mgmt',
  templateUrl: './user-management.component.html',
})
export class UserManagementComponent implements OnInit, OnDestroy {
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
    private router: Router,
    private eventManager: JhiEventManager,
    private modalService: NgbModal,
    private certificateService: CertificateService,
    private fb: FormBuilder,
    private alertService: JhiAlertService,
    private toastrService: ToastrService,
    public translateService: TranslateService
  ) {}

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => (this.currentAccount = account));
    this.userListSubscription = this.eventManager.subscribe('userListModification', () => {
      this.loadAll();
    });
    this.handleNavigation();
  }

  ngOnDestroy(): void {
    if (this.userListSubscription) {
      this.eventManager.destroy(this.userListSubscription);
    }
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }

  setActive(user: User, isActivated: boolean): void {
    this.userService.update({ ...user, activated: isActivated }).subscribe(() => this.loadAll());
  }

  trackIdentity(index: number, item: User): any {
    return item.id;
  }

  deleteUser(user: User): void {
    const modalRef = this.modalService.open(UserManagementDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.user = user;
  }

  viewCertificate(user: User): void {
    const modalRef = this.modalService.open(UserManagementViewCertificateComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.user = user;
  }

  searchUser(page?: number): any {
    const data = {
      ...this.userSearch.value,
      page: this.page - 1,
      size: this.itemsPerPage,
      sort: this.sort(),
    };
    const pageToLoad: number = page || this.page || 1;
    // const jsonData = JSON.stringify(data);
    // for (let i = 0; i < jsonData.length; i++){
    //   if (jsonData[i] != null) {
    //     jsonData[i] === jsonData[i].trim();
    //     }
    // }

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

    // console.error(jsonData);
    // console.error(data);
    this.userService.findByUser(data).subscribe((res: HttpResponse<User[]>) => this.onSuccess(res.body, res.headers));
  }

  transition(): void {
    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute.parent,
      queryParams: {
        page: this.page,
        sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
      },
    });
  }

  private handleNavigation(): void {
    combineLatest(this.activatedRoute.data, this.activatedRoute.queryParamMap, (data: Data, params: ParamMap) => {
      const page = params.get('page');
      this.page = page !== null ? +page : 1;
      const sort = (params.get('sort') ?? data['defaultSort']).split(',');
      this.predicate = sort[0];
      this.ascending = sort[1] === 'asc';
      this.searchUser();
    }).subscribe();
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

  // check or unckeck all elements of checkbox
  checkAll(row: any): void {
    const elements = document.getElementsByName('checkboxElement');
    if (row.target.checked) {
      elements.forEach(element => {
        element['checked'] = true;
      });
      // push all id to listId sent to backed
      elements.forEach((inputRow: any) => {
        this.listId.push(Number(inputRow.value));
      });
    } else {
      elements.forEach(element => {
        element['checked'] = false;
      });
      // reset list id
      this.listId = [];
    }
  }

  // change select event: add or remove into listId when click each checkbox element
  changeSelect(row: any): void {
    if (row.target.checked) {
      this.listId.push(Number(row.target.value));
    } else {
      const index: number = this.listId.indexOf(Number(row.target.value));
      if (index !== -1) {
        this.listId.splice(index, 1);
      }
    }
  }

  createCSR(): void {
    const modalRef = this.modalService.open(UserManagementKeyLengthComponent, { size: 'lg', backdrop: 'static' });
    this.userService.setListId(this.listId);
    // if (this.listId.length > 0) {
    //   this.certificateService
    //     .sendData({
    //       userIds: this.listId,
    //     })
    //     .subscribe((response: any) => {
    //       if (response.byteLength === 0) {
    //         this.toastrService.error(this.translateService.instant('userManagement.alert.fail.csrExported'));
    //       } else {
    //         this.toastrService.success(this.translateService.instant('userManagement.alert.success.csrExported'));
    //         saveAs(new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }), 'excel.xlsx');
    //       }
    //     });
    // }
  }

  downLoadFileTemplate(): void {
    this.userService.downLoadTemplateFile().subscribe(
      res => {
        const bindData = [];
        bindData.push(res.data);
        const url = window.URL.createObjectURL(
          new Blob(bindData, { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        );
        const a = document.createElement('a');
        document.body.appendChild(a);
        a.setAttribute('style', 'display: none');
        a.setAttribute('target', 'blank');
        a.href = url;
        a.download = 'templateFile';
        a.click();
        window.URL.revokeObjectURL(url);
        a.remove();
      },
      error => {
        console.error(error);
      }
    );
  }

  openModal(content: any): void {
    this.modalRef = this.modalService.open(content, { size: 'md' });
  }
  isUploadedSucessfully(agreed: boolean): void {
    if (agreed) {
      this.modalRef?.close();
      this.loadLastestRecord();
    }
  }
  loadLastestRecord(): void {
    const lastPage = Math.ceil(this.totalItems / ITEMS_PER_PAGE);

    this.userService
      .query({
        page: lastPage - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<IUser[]>) => this.onSuccess(res.body, res.headers),
        () => this.onError()
      );
  }
}
