import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { WebappTestModule } from '../../../test.module';
import { SignatureImageUpdateComponent } from 'app/entities/signature-image/signature-image-update.component';
import { SignatureImageService } from 'app/entities/signature-image/signature-image.service';
import { SignatureImage } from 'app/shared/model/signature-image.model';

describe('Component Tests', () => {
  describe('SignatureImage Management Update Component', () => {
    let comp: SignatureImageUpdateComponent;
    let fixture: ComponentFixture<SignatureImageUpdateComponent>;
    let service: SignatureImageService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [WebappTestModule],
        declarations: [SignatureImageUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(SignatureImageUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SignatureImageUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SignatureImageService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new SignatureImage(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new SignatureImage();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
