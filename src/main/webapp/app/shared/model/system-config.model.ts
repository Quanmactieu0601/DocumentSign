export interface ISystemConfig {
  id?: number;
  comId?: number;
  key?: string;
  value?: string;
  description?: string;
  dataType?: string;
  activated?: boolean;
}

export class SystemConfig implements ISystemConfig {
  constructor(
    public id?: number,
    public comId?: number,
    public key?: string,
    public value?: string,
    public description?: string,
    public dataType?: string,
    public activated?: boolean
  ) {
    this.activated = this.activated || false;
  }
}
