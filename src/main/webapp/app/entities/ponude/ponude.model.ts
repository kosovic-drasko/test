export interface IPonude {
  id: number;
  sifraPonude?: number | null;
  ponudjenaVrijednost?: number | null;
}

export type NewPonude = Omit<IPonude, 'id'> & { id: null };
