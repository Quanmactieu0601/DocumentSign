import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { WebappTestModule } from '../../../test.module';
import { SystemConfigCategoryUpdateComponent } from 'app/entities/system-config-category/system-config-category-update.component';
import { SystemConfigCategoryService } from 'app/entities/system-config-category/system-config-category.service';
import { SystemConfigCategory } from 'app/shared/model/system-config-category.model';

describe('Component Tests', () => {
  describe('SystemConfigCategory Management Update Component', () => {
    let comp: SystemConfigCategoryUpdateComponent;
    let fixture: ComponentFixture<SystemConfigCategoryUpdateComponent>;
    let service: SystemConfigCategoryService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [WebappTestModule],
        declarations: [SystemConfigCategoryUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(SystemConfigCategoryUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SystemConfigCategoryUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SystemConfigCategoryService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new SystemConfigCategory(123);
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
        const entity = new SystemConfigCategory();
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
