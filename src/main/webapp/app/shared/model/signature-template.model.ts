export interface ISignatureTemplate {
  id?: number;
  createdBy?: string;
  coreParser?: string;
  createdDate?: Date;
  htmlTemplate?: string;
  fullName?: string;
  userId?: number;
  width?: number;
  height?: number;
  transparency?: boolean;
  thumbnail?: string;
}

export class SignatureTemplate implements ISignatureTemplate {
  constructor(
    public id?: number,
    public htmlTemplate?: string,
    public userId?: number,
    public createdDate?: Date,
    public createdBy?: string,
    public fullName?: string,
    public coreParser?: string,
    public width?: number,
    public height?: number,
    public transparency?: boolean,
    public thumbnail?: string
  ) {}
}
