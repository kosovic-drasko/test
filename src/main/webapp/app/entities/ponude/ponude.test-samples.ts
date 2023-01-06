import { IPonude, NewPonude } from './ponude.model';

export const sampleWithRequiredData: IPonude = {
  id: 94075,
};

export const sampleWithPartialData: IPonude = {
  id: 80246,
};

export const sampleWithFullData: IPonude = {
  id: 18270,
  sifraPonude: 43908,
  ponudjenaVrijednost: 62815,
};

export const sampleWithNewData: NewPonude = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
