import { Moment } from 'moment';

export interface ICertPackage {
  id?: number;
  packageCode?: string;
  certType?: number;
  nameCert?: string;
  keyLength?: number;
  expiredDate?: Moment;
  signingTurn?: number;
  price?: number;
}

export class CertPackage implements ICertPackage {
  constructor(
    public id?: number,
    public packageCode?: string,
    public certType?: number,
    public nameCert?: string,
    public keyLength?: number,
    public expiredDate?: Moment,
    public signingTurn?: number,
    public price?: number
  ) {}
}
