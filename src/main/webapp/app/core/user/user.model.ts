export interface IUser {
  id?: any;
  login?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  ownerId?: any;
  phone?: string;
  commonName?: string;
  organizationName?: string;
  organizationUnit?: string;
  localityName?: string;
  stateName?: string;
  country?: string;
  activated?: boolean;
  langKey?: string;
  authorities?: string[];
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  password?: string;
}

export class User implements IUser {
  constructor(
    public id?: any,
    public login?: string,
    public firstName?: string,
    public lastName?: string,
    public email?: string,
    public ownerId?: any,
    public phone?: string,
    public commonName?: string,
    public organizationName?: string,
    public organizationUnit?: string,
    public localityName?: string,
    public stateName?: string,
    public country?: string,
    public activated?: boolean,
    public langKey?: string,
    public authorities?: string[],
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public password?: string,
    public currentPassword?: string
  ) {}
}
