import { Component, OnInit } from '@angular/core';
import { ChartType, ChartOptions } from 'chart.js';
import { Label } from 'ng2-charts';
import * as pluginDataLabels from 'chartjs-plugin-datalabels';
import { HttpResponse } from '@angular/common/http';
import { TransactionService } from 'app/entities/transaction/transaction.service';
import { FormBuilder } from '@angular/forms';
@Component({
  selector: 'jhi-transaction-report',
  templateUrl: './transaction-report.component.html',
  styleUrls: ['./transaction-report.component.scss'],
})
export class TransactionReportComponent implements OnInit {
  userSearch = this.fb.group({
    startDate: '',
    endDate: '',
    type: '',
  });
  totalsuccess = '';
  totalfail = '';
  public pieChartOptions: ChartOptions = {
    responsive: true,
    legend: {
      position: 'top',
    },
  };

  public pieChartLabels: Label[] = [['Tổng số lỗi'], ['Tổng số requet thành công']];
  public pieChartData: number[] = [];
  public pieChartType: ChartType = 'pie';
  public pieChartLegend = true;
  public pieChartPlugins = [pluginDataLabels];
  public pieChartColors = [
    {
      backgroundColor: ['rgb(255,100,0)', 'rgba(0 ,255 ,0)'],
    },
  ];
  constructor(protected transactionService: TransactionService, private fb: FormBuilder) {}

  ngOnInit(): void {}
  searchUser(): void {
    const data = {
      startDate: this.userSearch.get(['startDate'])!.value,
      endDate: this.userSearch.get(['endDate'])!.value,
      type: this.userSearch.get(['type'])!.value,
    };
    this.transactionService.queryTransaction(data.startDate, data.endDate, data.type).subscribe((res: HttpResponse<any>) => {
      this.totalsuccess = res.body.totalsuccess;
      this.totalfail = res.body.totalfail;
      this.pieChartData = [parseInt(this.totalfail, 10), parseInt(this.totalsuccess, 10)];
    });
  }
}
