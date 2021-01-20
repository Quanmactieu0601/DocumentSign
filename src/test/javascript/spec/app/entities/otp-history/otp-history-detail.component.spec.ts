import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { WebappTestModule } from '../../../test.module';
import { OtpHistoryDetailComponent } from 'app/entities/otp-history/otp-history-detail.component';
import { OtpHistory } from 'app/shared/model/otp-history.model';

describe('Component Tests', () => {
  describe('OtpHistory Management Detail Component', () => {
    let comp: OtpHistoryDetailComponent;
    let fixture: ComponentFixture<OtpHistoryDetailComponent>;
    const route = ({ data: of({ otpHistory: new OtpHistory(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [WebappTestModule],
        declarations: [OtpHistoryDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(OtpHistoryDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(OtpHistoryDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load otpHistory on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.otpHistory).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
