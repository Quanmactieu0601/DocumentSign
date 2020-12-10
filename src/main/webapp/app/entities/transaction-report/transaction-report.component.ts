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
    let date_time = new Date();
    let date = ('0' + date_time.getDate()).slice(-2);
    let month = ('0' + (date_time.getMonth() + 1)).slice(-2);
    let year = date_time.getFullYear();
    let hours = ('0' + date_time.getHours()).slice(-2);
    let minutes = ('0' + date_time.getMinutes()).slice(-2);
    let seconds = ('0' + date_time.getSeconds()).slice(-2);
    let time = year + month + date + ' ' + hours + minutes + seconds;
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
    });
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
