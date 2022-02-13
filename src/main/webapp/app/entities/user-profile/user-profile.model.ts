import { IUser } from 'app/entities/user/user.model';

export interface IUserProfile {
  id?: number;
  name?: string | null;
  avatarContentType?: string | null;
  avatar?: string | null;
  interests?: string | null;
  profession?: string | null;
  industry?: string | null;
  user?: IUser;
}

export class UserProfile implements IUserProfile {
  constructor(
    public id?: number,
    public name?: string | null,
    public avatarContentType?: string | null,
    public avatar?: string | null,
    public interests?: string | null,
    public profession?: string | null,
    public industry?: string | null,
    public user?: IUser
  ) {}
}

export function getUserProfileIdentifier(userProfile: IUserProfile): number | undefined {
  return userProfile.id;
}
