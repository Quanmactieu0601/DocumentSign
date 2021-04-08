export interface ICoreParser {
  id?: number;
  name?: string;
}

export class CoreParser implements ICoreParser {
  constructor(public id?: number, public name?: string) {}
}
