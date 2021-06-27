import { IA } from 'app/entities/a/a.model';

export interface IB {
  id?: number;
  name?: string | null;
  a?: IA | null;
}

export class B implements IB {
  constructor(public id?: number, public name?: string | null, public a?: IA | null) {}
}

export function getBIdentifier(b: IB): number | undefined {
  return b.id;
}
