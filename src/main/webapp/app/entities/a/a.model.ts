import { IB } from 'app/entities/b/b.model';

export interface IA {
  id?: number;
  name?: string | null;
  bs?: IB[] | null;
}

export class A implements IA {
  constructor(public id?: number, public name?: string | null, public bs?: IB[] | null) {}
}

export function getAIdentifier(a: IA): number | undefined {
  return a.id;
}
