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
      labels: {
        fontSize: 25,
        fontColor: 'gray',
      },
    },
  };
  public show = false;
  public showAlert = false;
  public pieChartLabels: Label[] = [['Tổng số lỗi'], ['Tổng số requet thành công']];
  public pieChartData: number[] = [];
  public pieChartType: ChartType = 'pie';
  public pieChartLegend = true;
  public pieChartPlugins = [pluginDataLabels];
  public pieChartColors = [
    {
      backgroundColor: ['rgb(255,100,0)', '#4285F4'],
    },
  ];

  constructor(protected transactionService: TransactionService, private fb: FormBuilder) {}

  ngOnInit(): void {}

  getTime(): any {
    const dateTime = new Date();
    const date = ('0' + dateTime.getDate()).slice(-2);
    const month = ('0' + (dateTime.getMonth() + 1)).slice(-2);
    const year = dateTime.getFullYear();
    const hours = ('0' + dateTime.getHours()).slice(-2);
    const minutes = ('0' + dateTime.getMinutes()).slice(-2);
    const seconds = ('0' + dateTime.getSeconds()).slice(-2);
    const time = year + month + date + ' ' + hours + minutes + seconds;
    return time;
  }

  ExportDPF(): void {
    const dataExport = {
      startDate: this.userSearch.get(['startDate'])!.value,
      endDate: this.userSearch.get(['endDate'])!.value,
      type: this.userSearch.get(['type'])!.value,
    };
    this.transactionService.exportPDFfromjasper(dataExport.startDate, dataExport.endDate, dataExport.type).subscribe(res => {
      const blob = new Blob([res], { type: 'application/pdf' });
      const downloadURL = window.URL.createObjectURL(res);
      const link = document.createElement('a');
      link.href = downloadURL;
      link.download = 'Transaction Report ' + this.getTime() + '.pdf';
      link.click();
      setTimeout(() => {
        this.showAlert = true;
      }, 2000);
    });
  }

  alertNoification() {
    this.showAlert = false;
  }

  searchUser(): void {
    const data = {
      startDate: this.userSearch.get(['startDate'])!.value,
      endDate: this.userSearch.get(['endDate'])!.value,
      type: this.userSearch.get(['type'])!.value,
    };
    this.show = true;
    this.transactionService.queryTransaction(data.startDate, data.endDate, data.type).subscribe((res: HttpResponse<any>) => {
      this.totalsuccess = res.body.TotalSuccess;
      this.totalfail = res.body.TotalFail;
      this.pieChartData = [parseInt(this.totalfail, 10), parseInt(this.totalsuccess, 10)];
    });
  }
}
