export interface IStudent {
  id: number;
  name?: string | null;
  age?: number | null;
}

export type NewStudent = Omit<IStudent, 'id'> & { id: null };
