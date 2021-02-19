import { ElementVM } from 'app/shared/model/elementVM.model';

export interface ISignatureVfVM {
  elements?: ElementVM[];
  hashAlgorithm?: string;
  serial?: string;
  base64Certificate?: string;
}

export class SignatureVfVM implements ISignatureVfVM {
  constructor(
    public base64Certificate?: string,
    public elements?: ElementVM[],
    public serial?: string,
    public hashAlgorithm?: string,
  ) {
    this.hashAlgorithm = this.hashAlgorithm || 'SHA1';
  }
}
