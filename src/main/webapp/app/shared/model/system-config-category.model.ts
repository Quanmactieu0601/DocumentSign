export interface ISystemConfigCategory {
  id?: number;
  configKey?: string;
  dataType?: string;
  description?: string;
}

export class SystemConfigCategory implements ISystemConfigCategory {
  constructor(public id?: number, public configKey?: string, public dataType?: string, public description?: string) {}
}
