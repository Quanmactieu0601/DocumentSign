import { Component, OnInit } from '@angular/core';
import { ChartOptions, ChartType } from 'chart.js';
import { Label } from 'ng2-charts';
import * as pluginDataLabels from 'chartjs-plugin-datalabels';
import { HttpResponse } from '@angular/common/http';
import { TransactionService } from 'app/entities/transaction/transaction.service';
import { FormBuilder } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { Type } from 'app/shared/constants/transaction.constants';
import { Router } from '@angular/router';

@Component({
  selector: 'jhi-transaction-report',
  templateUrl: './transaction-report.component.html',
})
export class TransactionReportComponent implements OnInit {
  userSearch = this.fb.group({
    startDate: [''],
    endDate: [''],
    type: [''],
  });

  type = Type;
  date = '';
  totalSuccess = '';
  totalFail = '';
  public pieChartOptions: ChartOptions = {
    responsive: true,
    legend: {
      position: 'top',
      labels: {
        fontSize: 25,
        fontColor: 'gray',
      },
    },
  };
  public show = false;
  // public showAlert = false;
  public pieChartLabels: Label[] = [
    this.translate.instant('webappApp.transactionReport.requestFail'),
    this.translate.instant('webappApp.transactionReport.requestSuccess'),
  ];
  public pieChartData: number[] = [];
  public pieChartType: ChartType = 'pie';
  public pieChartLegend = true;
  public pieChartPlugins = [pluginDataLabels];
  public pieChartColors = [
    {
      backgroundColor: ['rgb(255,100,0)', '#4285F4'],
    },
  ];

  constructor(
    protected transactionService: TransactionService,
    private fb: FormBuilder,
    private toastService: ToastrService,
    private translate: TranslateService,
    private router: Router
  ) {}

  ngOnInit(): void {}

  getTime(): any {
    const dateTime = new Date();
    const date = ('0' + dateTime.getDate()).slice(-2);
    const month = ('0' + (dateTime.getMonth() + 1)).slice(-2);
    const year = dateTime.getFullYear();
    const hours = ('0' + dateTime.getHours()).slice(-2);
    const minutes = ('0' + dateTime.getMinutes()).slice(-2);
    const seconds = ('0' + dateTime.getSeconds()).slice(-2);
    return year + month + date + ' ' + hours + minutes + seconds;
  }

  ExportDPF(): void {
    const dataExport = {
      startDate: this.userSearch.get(['startDate'])!.value,
      endDate: this.userSearch.get(['endDate'])!.value,
      type: this.userSearch.get(['type'])!.value,
    };
    this.transactionService.exportPDFfromjasper(dataExport.startDate, dataExport.endDate, dataExport.type).subscribe(res => {
      // const blob = new Blob([res], { type: 'application/pdf' });
      const downloadURL = window.URL.createObjectURL(res);
      const link = document.createElement('a');
      link.href = downloadURL;
      link.download = 'Transaction Report ' + this.getTime() + '.pdf';
      link.click();
      this.toastService.success(this.translate.instant('webappApp.transactionReport.success'));
      // this.showAlert = true;
      // setTimeout(() => {
      //   this.showAlert = false;
      // }, 4000);
    });
  }

  searchUser(): void {
    const startDate = this.userSearch.get(['startDate'])!.value;
    const endDate = this.userSearch.get(['endDate'])!.value;
    const type = this.userSearch.get(['type'])!.value;
    this.show = true;
    this.transactionService.queryTransaction(startDate, endDate, type).subscribe((res: HttpResponse<any>) => {
      this.totalSuccess = res.body.TotalSuccess;
      this.totalFail = res.body.TotalFail;
      this.pieChartData = [parseInt(this.totalFail, 10), parseInt(this.totalSuccess, 10)];
    });
  }
}
