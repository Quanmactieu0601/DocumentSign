import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { WebappTestModule } from '../../../test.module';
import { OtpHistoryUpdateComponent } from 'app/entities/otp-history/otp-history-update.component';
import { OtpHistoryService } from 'app/entities/otp-history/otp-history.service';
import { OtpHistory } from 'app/shared/model/otp-history.model';

describe('Component Tests', () => {
  describe('OtpHistory Management Update Component', () => {
    let comp: OtpHistoryUpdateComponent;
    let fixture: ComponentFixture<OtpHistoryUpdateComponent>;
    let service: OtpHistoryService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [WebappTestModule],
        declarations: [OtpHistoryUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(OtpHistoryUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(OtpHistoryUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(OtpHistoryService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new OtpHistory(123);
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
        const entity = new OtpHistory();
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
