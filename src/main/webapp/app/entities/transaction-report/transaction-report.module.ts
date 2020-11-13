import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TransactionReportComponent } from 'app/entities/transaction-report/transaction-report.component';
import { transactionReportRoute } from 'app/entities/transaction-report/transaction-report.route';
import { ChartsModule } from 'ng2-charts';
import { WebappSharedModule } from 'app/shared/shared.module';
@NgModule({
  imports: [RouterModule.forChild(transactionReportRoute), ChartsModule, WebappSharedModule],
  declarations: [TransactionReportComponent],
})
export class TransactionReportModule {}
