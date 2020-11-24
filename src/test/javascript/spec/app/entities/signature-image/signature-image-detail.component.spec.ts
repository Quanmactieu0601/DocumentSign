import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { WebappTestModule } from '../../../test.module';
import { SignatureImageDetailComponent } from 'app/entities/signature-image/signature-image-detail.component';
import { SignatureImage } from 'app/shared/model/signature-image.model';

describe('Component Tests', () => {
  describe('SignatureImage Management Detail Component', () => {
    let comp: SignatureImageDetailComponent;
    let fixture: ComponentFixture<SignatureImageDetailComponent>;
    const route = ({ data: of({ signatureImage: new SignatureImage(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [WebappTestModule],
        declarations: [SignatureImageDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(SignatureImageDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SignatureImageDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load signatureImage on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.signatureImage).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
