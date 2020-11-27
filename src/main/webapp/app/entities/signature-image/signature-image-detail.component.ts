import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISignatureImage } from 'app/shared/model/signature-image.model';

@Component({
  selector: 'jhi-signature-image-detail',
  templateUrl: './signature-image-detail.component.html',
})
export class SignatureImageDetailComponent implements OnInit {
  signatureImage: ISignatureImage | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ signatureImage }) => (this.signatureImage = signatureImage));
  }

  previousState(): void {
    window.history.back();
  }
}
