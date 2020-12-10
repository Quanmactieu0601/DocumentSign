import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { WebappTestModule } from '../../../test.module';
import { SystemConfigUpdateComponent } from 'app/entities/system-config/system-config-update.component';
import { SystemConfigService } from 'app/entities/system-config/system-config.service';
import { SystemConfig } from 'app/shared/model/system-config.model';

describe('Component Tests', () => {
  describe('SystemConfig Management Update Component', () => {
    let comp: SystemConfigUpdateComponent;
    let fixture: ComponentFixture<SystemConfigUpdateComponent>;
    let service: SystemConfigService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [WebappTestModule],
        declarations: [SystemConfigUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(SystemConfigUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SystemConfigUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SystemConfigService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new SystemConfig(123);
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
        const entity = new SystemConfig();
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
