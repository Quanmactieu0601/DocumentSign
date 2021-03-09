import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { ISignatureVfVM } from 'app/shared/model/signatureVfVM.model';

@Injectable({ providedIn: 'root' })
export class VerifySignatureService {
  public resourceUrl = SERVER_API_URL + 'api/verification';

  constructor(protected http: HttpClient) {}

  verifyDoc(file: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file);

    const req = new HttpRequest('POST', `${this.resourceUrl}/doc`, formData, {
      reportProgress: true,
      responseType: 'json',
    });

    return this.http.request(req);
  }

  verifyPdf(file: File): Observable<any> {
    const formData: FormData = new FormData();

    formData.append('file', file);

    const req = new HttpRequest('POST', `${this.resourceUrl}/pdf`, formData, {
      reportProgress: true,
      responseType: 'json',
    });

    return this.http.request(req);
  }

  verifyRaw(signatureVfVM: ISignatureVfVM): Observable<any> {
    return this.http.post<ISignatureVfVM>(this.resourceUrl + '/raw', signatureVfVM, { observe: 'response' });
  }

  verifyHash(signatureVfVM: ISignatureVfVM): Observable<any> {
    return this.http.post<ISignatureVfVM>(this.resourceUrl + '/hash', signatureVfVM, { observe: 'response' });
  }
}
