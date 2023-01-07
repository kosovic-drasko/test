import { IStudent, NewStudent } from './student.model';

export const sampleWithRequiredData: IStudent = {
  id: 23105,
  name: 'Franc Division Regional',
  age: 65321,
};

export const sampleWithPartialData: IStudent = {
  id: 10501,
  name: 'seamless Garden transmit',
  age: 36422,
};

export const sampleWithFullData: IStudent = {
  id: 70908,
  name: 'orchid ivory',
  age: 39489,
};

export const sampleWithNewData: NewStudent = {
  name: 'Nebraska Carolina',
  age: 2882,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
