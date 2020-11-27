import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISignatureTemplate } from 'app/shared/model/signature-template.model';

@Component({
  selector: 'jhi-signature-template-detail',
  templateUrl: './signature-template-detail.component.html',
})
export class SignatureTemplateDetailComponent implements OnInit {
  signatureTemplate: ISignatureTemplate | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ signatureTemplate }) => (this.signatureTemplate = signatureTemplate));
  }

  previousState(): void {
    window.history.back();
  }
}
