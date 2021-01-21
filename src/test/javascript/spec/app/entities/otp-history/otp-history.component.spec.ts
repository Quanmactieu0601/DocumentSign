import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { WebappTestModule } from '../../../test.module';
import { OtpHistoryComponent } from 'app/entities/otp-history/otp-history.component';
import { OtpHistoryService } from 'app/entities/otp-history/otp-history.service';
import { OtpHistory } from 'app/shared/model/otp-history.model';

describe('Component Tests', () => {
  describe('OtpHistory Management Component', () => {
    let comp: OtpHistoryComponent;
    let fixture: ComponentFixture<OtpHistoryComponent>;
    let service: OtpHistoryService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [WebappTestModule],
        declarations: [OtpHistoryComponent],
      })
        .overrideTemplate(OtpHistoryComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(OtpHistoryComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(OtpHistoryService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new OtpHistory(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.otpHistories && comp.otpHistories[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
