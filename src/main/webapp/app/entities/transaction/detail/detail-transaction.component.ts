import { Component, OnInit } from '@angular/core';
import { ITransaction } from 'app/shared/model/transaction.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'jhi-detailtransaction',
  templateUrl: './detail-transaction.component.html',
})
export class DetailTransactionComponent implements OnInit {
  transaction?: ITransaction;

  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {}

  cancel(): void {
    this.activeModal.dismiss();
  }
}
