import DateTimeFormat = Intl.DateTimeFormat;

export interface ICertificate {
  id?: number;
  lastUpdate?: string;
  tokenType?: string;
  serial?: string;
  ownerTaxcode?: string;
  subjectInfo?: string;
  alias?: string;
  tokenInfo?: string;
  rawData?: string;
  validDate?: Date;
  expiredDate?: Date;
  activeStatus?: number;
}

export class Certificate implements ICertificate {
  constructor(
    public id?: number,
    public lastUpdate?: string,
    public tokenType?: string,
    public serial?: string,
    public ownerTaxcode?: string,
    public subjectInfo?: string,
    public alias?: string,
    public tokenInfo?: string,
    public rawData?: string,
    public validDate?: Date,
    public expiredDate?: Date,
    public activeStatus?: number
  ) {}
}
