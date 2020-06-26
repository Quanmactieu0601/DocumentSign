import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICertificate } from 'app/shared/model/certificate.model';

@Component({
  selector: 'jhi-certificate-detail',
  templateUrl: './certificate-detail.component.html',
})
export class CertificateDetailComponent implements OnInit {
  certificate: ICertificate | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ certificate }) => (this.certificate = certificate));
  }

  previousState(): void {
    window.history.back();
  }
}
