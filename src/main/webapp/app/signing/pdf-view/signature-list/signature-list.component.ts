import { Component, Input, OnInit } from '@angular/core';
import { SignatureTemplateService } from 'app/entities/signature-template/signature-template.service';
import { ISignatureTemplate } from 'app/shared/model/signature-template.model';
import { HttpResponse } from '@angular/common/http';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'jhi-signature-list',
  templateUrl: './signature-list.component.html',
  styleUrls: ['./signature-list.component.scss'],
})
export class SignatureListComponent implements OnInit {
  @Input() userId: any;
  templates: ISignatureTemplate[] | null | undefined;

  constructor(protected signatureTemplateService: SignatureTemplateService, public activeModal: NgbActiveModal) {}

  ngOnInit(): void {
    this.signatureTemplateService.getSignatureTemplateByUserID(this.userId).subscribe((res: HttpResponse<ISignatureTemplate[]>) => {
      this.templates = res.body;
    });
  }
}
