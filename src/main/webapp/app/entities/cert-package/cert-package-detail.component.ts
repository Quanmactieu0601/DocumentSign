import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICertPackage } from 'app/shared/model/cert-package.model';

@Component({
  selector: 'jhi-cert-package-detail',
  templateUrl: './cert-package-detail.component.html',
})
export class CertPackageDetailComponent implements OnInit {
  certPackage: ICertPackage | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ certPackage }) => (this.certPackage = certPackage));
  }

  previousState(): void {
    window.history.back();
  }
}
