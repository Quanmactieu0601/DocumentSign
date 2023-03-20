export interface IRsCertificate {
  id?: number;
  lastUpdate?: string;
  serial?: string;
  ownerTaxcode?: string;
  subjectInfo?: string;
  rawData?: string;
  validDate?: Date;
  expiredDate?: Date;
  activeStatus?: number;
  type?: number;
  signingCount?: number;
  authMode?: string;
}

export class Certificate implements IRsCertificate {
  constructor(
    public id?: number,
    public lastUpdate?: string,
    public serial?: string,
    public ownerTaxcode?: string,
    public subjectInfo?: string,
    public rawData?: string,
    public validDate?: Date,
    public expiredDate?: Date,
    public activeStatus?: number,
    public type?: number,
    public signingCount?: number,
    public authMode?: string
  ) {}
}
