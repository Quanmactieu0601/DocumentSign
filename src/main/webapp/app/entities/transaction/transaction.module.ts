import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { WebappSharedModule } from 'app/shared/shared.module';
import { TransactionComponent } from './transaction.component';
import { TransactionDetailComponent } from './transaction-detail.component';
import { TransactionUpdateComponent } from './transaction-update.component';
import { TransactionDeleteDialogComponent } from './transaction-delete-dialog.component';
import { transactionRoute } from './transaction.route';
import { DetailTransactionComponent } from 'app/entities/transaction/detail/detail-transaction.component';

@NgModule({
  imports: [WebappSharedModule, RouterModule.forChild(transactionRoute)],
  declarations: [
    TransactionComponent,
    TransactionDetailComponent,
    TransactionUpdateComponent,
    TransactionDeleteDialogComponent,
    DetailTransactionComponent,
  ],
  entryComponents: [TransactionDeleteDialogComponent],
})
export class WebappTransactionModule {}
