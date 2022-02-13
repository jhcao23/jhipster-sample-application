import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IUserProfileInterest, UserProfileInterest } from '../user-profile-interest.model';

import { UserProfileInterestService } from './user-profile-interest.service';

describe('UserProfileInterest Service', () => {
  let service: UserProfileInterestService;
  let httpMock: HttpTestingController;
  let elemDefault: IUserProfileInterest;
  let expectedResult: IUserProfileInterest | IUserProfileInterest[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(UserProfileInterestService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      code: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a UserProfileInterest', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new UserProfileInterest()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a UserProfileInterest', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          code: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a UserProfileInterest', () => {
      const patchObject = Object.assign(
        {
          code: 'BBBBBB',
        },
        new UserProfileInterest()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of UserProfileInterest', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          code: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a UserProfileInterest', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addUserProfileInterestToCollectionIfMissing', () => {
      it('should add a UserProfileInterest to an empty array', () => {
        const userProfileInterest: IUserProfileInterest = { id: 123 };
        expectedResult = service.addUserProfileInterestToCollectionIfMissing([], userProfileInterest);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userProfileInterest);
      });

      it('should not add a UserProfileInterest to an array that contains it', () => {
        const userProfileInterest: IUserProfileInterest = { id: 123 };
        const userProfileInterestCollection: IUserProfileInterest[] = [
          {
            ...userProfileInterest,
          },
          { id: 456 },
        ];
        expectedResult = service.addUserProfileInterestToCollectionIfMissing(userProfileInterestCollection, userProfileInterest);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a UserProfileInterest to an array that doesn't contain it", () => {
        const userProfileInterest: IUserProfileInterest = { id: 123 };
        const userProfileInterestCollection: IUserProfileInterest[] = [{ id: 456 }];
        expectedResult = service.addUserProfileInterestToCollectionIfMissing(userProfileInterestCollection, userProfileInterest);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userProfileInterest);
      });

      it('should add only unique UserProfileInterest to an array', () => {
        const userProfileInterestArray: IUserProfileInterest[] = [{ id: 123 }, { id: 456 }, { id: 85502 }];
        const userProfileInterestCollection: IUserProfileInterest[] = [{ id: 123 }];
        expectedResult = service.addUserProfileInterestToCollectionIfMissing(userProfileInterestCollection, ...userProfileInterestArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const userProfileInterest: IUserProfileInterest = { id: 123 };
        const userProfileInterest2: IUserProfileInterest = { id: 456 };
        expectedResult = service.addUserProfileInterestToCollectionIfMissing([], userProfileInterest, userProfileInterest2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userProfileInterest);
        expect(expectedResult).toContain(userProfileInterest2);
      });

      it('should accept null and undefined values', () => {
        const userProfileInterest: IUserProfileInterest = { id: 123 };
        expectedResult = service.addUserProfileInterestToCollectionIfMissing([], null, userProfileInterest, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userProfileInterest);
      });

      it('should return initial array if no UserProfileInterest is added', () => {
        const userProfileInterestCollection: IUserProfileInterest[] = [{ id: 123 }];
        expectedResult = service.addUserProfileInterestToCollectionIfMissing(userProfileInterestCollection, undefined, null);
        expect(expectedResult).toEqual(userProfileInterestCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
