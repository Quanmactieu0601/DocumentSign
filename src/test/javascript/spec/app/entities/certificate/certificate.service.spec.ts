import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ICertificate, Certificate } from 'app/shared/model/certificate.model';

describe('Service Tests', () => {
  describe('Certificate Service', () => {
    let injector: TestBed;
    let service: CertificateService;
    let httpMock: HttpTestingController;
    let elemDefault: ICertificate;
    let expectedResult: ICertificate | ICertificate[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(CertificateService);
      httpMock = injector.get(HttpTestingController);

      elemDefault = new Certificate(0, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA');
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Certificate', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Certificate()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Certificate', () => {
        const returnedFromService = Object.assign(
          {
            lastUpdate: 'BBBBBB',
            tokenType: 'BBBBBB',
            serial: 'BBBBBB',
            ownerTaxcode: 'BBBBBB',
            subjectInfo: 'BBBBBB',
            alias: 'BBBBBB',
            tokenInfo: 'BBBBBB',
            rawData: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Certificate', () => {
        const returnedFromService = Object.assign(
          {
            lastUpdate: 'BBBBBB',
            tokenType: 'BBBBBB',
            serial: 'BBBBBB',
            ownerTaxcode: 'BBBBBB',
            subjectInfo: 'BBBBBB',
            alias: 'BBBBBB',
            tokenInfo: 'BBBBBB',
            rawData: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Certificate', () => {
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
