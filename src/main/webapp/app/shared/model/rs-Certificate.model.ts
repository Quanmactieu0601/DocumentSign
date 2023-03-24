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
  signedTurnCount?: number;
  authMode?: string;
  signingProfile?: number;
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
    public signedTurnCount?: number,
    public authMode?: string,
    public signingProfile?: number
  ) {}
}
