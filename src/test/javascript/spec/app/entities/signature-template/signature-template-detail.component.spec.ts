import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { WebappTestModule } from '../../../test.module';
import { SignatureTemplateDetailComponent } from 'app/entities/signature-template/signature-template-detail.component';
import { SignatureTemplate } from 'app/shared/model/signature-template.model';

describe('Component Tests', () => {
  describe('SignatureTemplate Management Detail Component', () => {
    let comp: SignatureTemplateDetailComponent;
    let fixture: ComponentFixture<SignatureTemplateDetailComponent>;
    const route = ({ data: of({ signatureTemplate: new SignatureTemplate(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [WebappTestModule],
        declarations: [SignatureTemplateDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(SignatureTemplateDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SignatureTemplateDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load signatureTemplate on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.signatureTemplate).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
