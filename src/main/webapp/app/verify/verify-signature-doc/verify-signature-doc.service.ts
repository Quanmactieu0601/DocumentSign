import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpHeaders, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';

@Injectable({ providedIn: 'root' })
export class VerifySignatureDocService {
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
}
