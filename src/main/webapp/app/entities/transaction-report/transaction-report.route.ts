import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { TransactionReportComponent } from 'app/entities/transaction-report/transaction-report.component';

@Injectable({ providedIn: 'root' })
export class TransactionReportRoute {}

export const transactionReportRoute: Routes = [];
