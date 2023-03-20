import { Component, OnInit } from '@angular/core';
import { IRsCertificate } from 'app/shared/model/rs-Certificate.model';

@Component({
  selector: 'jhi-rs-certificate',
  templateUrl: './rs-certificate.component.html',
  styleUrls: ['./rs-certificate.component.scss'],
})
export class RsCertificateComponent implements OnInit {
  rsCertificates?: IRsCertificate[];

  constructor() {}

  ngOnInit(): void {}
}
