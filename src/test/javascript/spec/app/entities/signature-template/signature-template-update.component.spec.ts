import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { WebappTestModule } from '../../../test.module';
import { SignatureTemplateUpdateComponent } from 'app/entities/signature-template/signature-template-update.component';
import { SignatureTemplateService } from 'app/entities/signature-template/signature-template.service';
import { SignatureTemplate } from 'app/shared/model/signature-template.model';

describe('Component Tests', () => {
  describe('SignatureTemplate Management Update Component', () => {
    let comp: SignatureTemplateUpdateComponent;
    let fixture: ComponentFixture<SignatureTemplateUpdateComponent>;
    let service: SignatureTemplateService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [WebappTestModule],
        declarations: [SignatureTemplateUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(SignatureTemplateUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SignatureTemplateUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SignatureTemplateService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new SignatureTemplate(123);
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
        const entity = new SignatureTemplate();
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
