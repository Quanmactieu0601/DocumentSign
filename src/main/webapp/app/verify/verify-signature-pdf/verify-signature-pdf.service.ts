import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpHeaders, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';

@Injectable({ providedIn: 'root' })
export class VerifySignaturePdfService {
  public resourceUrl = SERVER_API_URL + 'api/verification';

  constructor(protected http: HttpClient) {}

  verifyPdf(file: File): Observable<any> {
    const formData: FormData = new FormData();

    formData.append('file', file);

    const req = new HttpRequest('POST', `${this.resourceUrl}/pdf`, formData, {
      reportProgress: true,
      responseType: 'json',
    });

    return this.http.request(req);
  }


}
