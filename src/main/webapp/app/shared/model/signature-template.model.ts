export interface ISignatureTemplate {
  id?: number;
  createdBy?: string;
  coreParser?: string;
  createdDate?: Date;
  htmlTemplate?: string;
  fullName?: string;
  userId?: number;
}

export class SignatureTemplate implements ISignatureTemplate {
  constructor(
    public id?: number,
    public htmlTemplate?: string,
    public userId?: number,
    public createdDate?: Date,
    public createdBy?: string,
    public fullName?: string,
    public coreParser?: string
  ) {}
}
