export interface ICertificateVfDTO {
  issuer?: string;
  subjectDn?: string;
  validFrom?: Date;
  validTo?: Date;
  revocationStatus?: string;
  signTimeStatus?: string;
  currentStatus?: string;
  easyCACert?: boolean;
}

export class CertificateVfDTO implements ICertificateVfDTO {
  constructor(
    public issuer?: string,
    public subjectDn?: string,
    public validFrom?: Date,
    public validTo?: Date,
    public revocationStatus?: string,
    public signTimeStatus?: string,
    public currentStatus?: string,
    public easyCACert?: boolean
  ) {}
}
