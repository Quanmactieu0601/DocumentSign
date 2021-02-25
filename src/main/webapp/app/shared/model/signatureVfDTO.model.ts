import { CertificateVfDTO } from 'app/shared/model/certificateVfDTO.model';

export interface ISignatureVfDTO {
  coverWholeDocument?: boolean;
  revision?: number;
  totalRevision?: number;
  signTime?: Date;
  certificateVfDTOs?: CertificateVfDTO[];
  integrity?: boolean;
}

export class SignatureVfDTO implements ISignatureVfDTO {
  constructor(
    public coverWholeDocument?: boolean,
    public revision?: number,
    public totalRevision?: number,
    public signTime?: Date,
    public certificateVfDTOs?: CertificateVfDTO[],
    public integrity?: boolean
  ) {}
}
