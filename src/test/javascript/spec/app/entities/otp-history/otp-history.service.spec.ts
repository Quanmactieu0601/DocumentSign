import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { OtpHistoryService } from 'app/entities/otp-history/otp-history.service';
import { IOtpHistory, OtpHistory } from 'app/shared/model/otp-history.model';

describe('Service Tests', () => {
  describe('OtpHistory Service', () => {
    let injector: TestBed;
    let service: OtpHistoryService;
    let httpMock: HttpTestingController;
    let elemDefault: IOtpHistory;
    let expectedResult: IOtpHistory | IOtpHistory[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(OtpHistoryService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new OtpHistory(0, 0, 0, 'AAAAAAA', 'AAAAAAA', currentDate, currentDate);
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            actionTime: currentDate.format(DATE_FORMAT),
            expireTime: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a OtpHistory', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            actionTime: currentDate.format(DATE_FORMAT),
            expireTime: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            actionTime: currentDate,
            expireTime: currentDate,
          },
          returnedFromService
        );

        service.create(new OtpHistory()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a OtpHistory', () => {
        const returnedFromService = Object.assign(
          {
            userId: 1,
            comId: 1,
            secretKey: 'BBBBBB',
            otp: 'BBBBBB',
            actionTime: currentDate.format(DATE_FORMAT),
            expireTime: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            actionTime: currentDate,
            expireTime: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of OtpHistory', () => {
        const returnedFromService = Object.assign(
          {
            userId: 1,
            comId: 1,
            secretKey: 'BBBBBB',
            otp: 'BBBBBB',
            actionTime: currentDate.format(DATE_FORMAT),
            expireTime: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            actionTime: currentDate,
            expireTime: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a OtpHistory', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
