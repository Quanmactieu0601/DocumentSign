import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, ParamMap, Router, Data } from '@angular/router';
import { Subscription, combineLatest } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ITransaction } from 'app/shared/model/transaction.model';
import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { TransactionService } from './transaction.service';
import { TransactionDeleteDialogComponent } from './transaction-delete-dialog.component';
import { FormBuilder } from '@angular/forms';
import { Action, Status } from 'app/shared/constants/transaction.constants';
import { DetailTransactionComponent } from 'app/entities/transaction/detail/detail-transaction.component';

@Component({
  selector: 'jhi-transaction',
  templateUrl: './transaction.component.html',
  styleUrls: ['./transaction.component.scss'],
})
export class TransactionComponent implements OnInit, OnDestroy {
  transactions: ITransaction[] | null = null;
  eventSubscriber?: Subscription;
  searchForm = this.fb.group({
    api: [],
    status: [],
    message: [],
    data: [],
    type: [],
    host: [],
    method: [],
    fullName: [],
    triggerTime: [],
    startDate: [],
    endDate: [],
    action: [],
    extension: [],
  });
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;

  status = Status;
  actions = Action;

  constructor(
    protected transactionService: TransactionService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected fb: FormBuilder
  ) {}

  loadPage(page?: number): void {
    this.searchTransactions(page);
  }

  ngOnInit(): void {
    this.handleNavigation();
    this.registerChangeInTransactions();
  }

  protected handleNavigation(): void {
    combineLatest(this.activatedRoute.data, this.activatedRoute.queryParamMap, (data: Data, params: ParamMap) => {
      const page = params.get('page');
      const pageNumber = page !== null ? +page : 1;
      const sort = (params.get('sort') ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === 'asc';
      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.searchTransactions();
      }
    }).subscribe();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ITransaction): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }
  registerChangeInTransactions(): void {
    this.eventSubscriber = this.eventManager.subscribe('transactionListModification', () => this.loadPage());
  }
  searchTransactions(page?: number): any {
    const pageToLoad: number = page || this.page || 1;
    const fieldTransaction = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.sort(),
      ...this.searchForm.value,
    };
    fieldTransaction.fullName = fieldTransaction.fullName ? fieldTransaction.fullName.trim() : null;
    fieldTransaction.action = fieldTransaction.action ? fieldTransaction.action.trim() : null;
    this.transactionService
      .findByTransaction(fieldTransaction)
      .subscribe((res: HttpResponse<any>) => this.onSuccess(res.body, res.headers, pageToLoad, false));
  }

  delete(transaction: ITransaction): void {
    const modalRef = this.modalService.open(TransactionDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.transaction = transaction;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'a.id') {
      result.push('a.id');
    }
    return result;
  }

  protected onSuccess(data: ITransaction[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/transaction'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
        },
      });
    }
    this.transactions = data || [];
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }

  detail(transaction: ITransaction): void {
    const modalRef = this.modalService.open(DetailTransactionComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.transaction = transaction;
  }

  transform(extension: string | undefined): string {
    switch (extension) {
      case 'RAW': {
        return 'ext-raw';
      }
      case 'PDF': {
        return 'ext-pdf';
      }
      case 'HASH': {
        return 'ext-hash';
      }
      case 'XML': {
        return 'ext-xml';
      }
      case 'OOXML': {
        return 'ext-ooml';
      }
      case 'CSR': {
        return 'ext-csr';
      }
      case 'CERT': {
        return 'ext-cert';
      }
      case 'SIGN_TEMPLATE': {
        return 'ext-sign-template';
      }
      case 'SIGN_IMAGE': {
        return 'ext-sign-image';
      }
      case 'ACCOUNT': {
        return 'ext-account';
      }
      case 'QR_CODE': {
        return 'ext-qr-code';
      }
      case 'NONE': {
        return 'ext-none';
      }
      default: {
        return '';
      }
    }
  }

  refresh() {
    let currentUrl = this.router.url;
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    this.router.onSameUrlNavigation = 'reload';
    this.router.navigate([currentUrl]);
  }
}
