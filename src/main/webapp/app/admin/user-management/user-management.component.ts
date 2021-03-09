import { Component, OnInit, OnDestroy, Output, EventEmitter } from '@angular/core';
import { HttpResponse, HttpHeaders, HttpEventType } from '@angular/common/http';
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
import { IUser, User } from '../../core/user/user.model';
import { UserManagementKeyLengthComponent } from './user-management-key-length.component';
import { UploadUserComponent } from 'app/admin/user-management/upload-user/upload-user-component';

@Component({
  selector: 'jhi-user-mgmt',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.scss'],
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

  // Update p12
  selectFiles: any;
  progress = 0;
  currentFile: any;
  account: Account | null = null;
  authSubscription?: Subscription;
  pin: string | undefined;
  fileName: string | undefined;

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
    //  Update p12
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));

    this.accountService.identity().subscribe(account => (this.currentAccount = account));
    this.userListSubscription = this.eventManager.subscribe('userListModification', () => {
      this.loadAll();
    });
    this.handleNavigation();
    this.fileName = 'Choose file ...';
  }

  // Update p12
  selectFile(event: any): void {
    this.selectFiles = event.target.files;
    const sizeFile = event.target.files.item(0).size / 1024000;
    if (sizeFile > 1) {
      this.toastrService.error(this.translateService.instant('userManagement.alert.fail.sizeErr'));
      this.selectFiles = [];
    }
    this.fileName = event.target.files[0].name;
  }

  uploadP12File(): void {
    this.progress = 0;
    this.currentFile = this.selectFiles.item(0);
    if (this.pin === undefined || this.pin === '') {
      this.toastrService.error('Must input pin');
    } else if (this.account != null) {
      if (this.pin === undefined) this.pin = '';
      this.userService.uploadP12(this.currentFile, this.pin).subscribe((res: any) => {
        if (res.type === HttpEventType.UploadProgress) {
          this.progress = Math.round((100 * res.loaded) / res.total);
        } else if (res instanceof HttpResponse) {
          if (res.body.status === 200) {
            this.toastrService.success(this.translateService.instant('userManagement.alert.success.uploaded'));
            this.isUploadedSucessfully(true);
          } else {
            this.toastrService.error(res.body.msg, '', {
              enableHtml: true,
            });
          }
        }
      });
    }
  }
  onInputClick = (event: any) => {
    const element = event.target as HTMLInputElement;
    element.value = '';
  };
  /**/

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
  }

  showUploadComponent(): void {
    this.modalRef = this.modalService.open(UploadUserComponent, { size: 'lg' });
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
    this.userService.setListId(this.listId);
    let userFilter: User[];
    if (this.users?.filter(user => this.listId.includes(user.id)) !== undefined) {
      userFilter = this.users?.filter(user => this.listId.includes(user.id));
      this.userService.setUsers(userFilter);
    }
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
