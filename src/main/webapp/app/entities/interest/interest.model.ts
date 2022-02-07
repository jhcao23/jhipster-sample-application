export interface IInterest {
  id?: number;
  name?: string | null;
  code?: string | null;
}

export class Interest implements IInterest {
  constructor(public id?: number, public name?: string | null, public code?: string | null) {}
}

export function getInterestIdentifier(interest: IInterest): number | undefined {
  return interest.id;
}
