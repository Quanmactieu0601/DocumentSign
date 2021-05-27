import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { SERVER_API_URL } from 'app/app.constants';
import { Observable } from 'rxjs';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  responseType: 'text' as 'text',
};
@Injectable({ providedIn: 'root' })
export class SigningService {
  constructor(private http: HttpClient) {}

  signPdf(req: any): Observable<any> {
    const requestData = JSON.stringify(req);
    return this.http.post(SERVER_API_URL + '/api/sign/pdf', requestData, httpOptions);
  }
  signRaw(req: any): Observable<any> {
    const requestData = JSON.stringify(req);
    return this.http.post(SERVER_API_URL + '/api/sign/raw', requestData, httpOptions);
  }
}
