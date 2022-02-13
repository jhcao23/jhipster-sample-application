import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { IInterest } from 'app/entities/interest/interest.model';

export interface IUserProfileInterest {
  id?: number;
  code?: string | null;
  userProfile?: IUserProfile;
  interest?: IInterest;
}

export class UserProfileInterest implements IUserProfileInterest {
  constructor(public id?: number, public code?: string | null, public userProfile?: IUserProfile, public interest?: IInterest) {}
}

export function getUserProfileInterestIdentifier(userProfileInterest: IUserProfileInterest): number | undefined {
  return userProfileInterest.id;
}
