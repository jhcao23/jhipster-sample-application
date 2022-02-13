import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUserProfileInterest, getUserProfileInterestIdentifier } from '../user-profile-interest.model';

export type EntityResponseType = HttpResponse<IUserProfileInterest>;
export type EntityArrayResponseType = HttpResponse<IUserProfileInterest[]>;

@Injectable({ providedIn: 'root' })
export class UserProfileInterestService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/user-profile-interests');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(userProfileInterest: IUserProfileInterest): Observable<EntityResponseType> {
    return this.http.post<IUserProfileInterest>(this.resourceUrl, userProfileInterest, { observe: 'response' });
  }

  update(userProfileInterest: IUserProfileInterest): Observable<EntityResponseType> {
    return this.http.put<IUserProfileInterest>(
      `${this.resourceUrl}/${getUserProfileInterestIdentifier(userProfileInterest) as number}`,
      userProfileInterest,
      { observe: 'response' }
    );
  }

  partialUpdate(userProfileInterest: IUserProfileInterest): Observable<EntityResponseType> {
    return this.http.patch<IUserProfileInterest>(
      `${this.resourceUrl}/${getUserProfileInterestIdentifier(userProfileInterest) as number}`,
      userProfileInterest,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IUserProfileInterest>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUserProfileInterest[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addUserProfileInterestToCollectionIfMissing(
    userProfileInterestCollection: IUserProfileInterest[],
    ...userProfileInterestsToCheck: (IUserProfileInterest | null | undefined)[]
  ): IUserProfileInterest[] {
    const userProfileInterests: IUserProfileInterest[] = userProfileInterestsToCheck.filter(isPresent);
    if (userProfileInterests.length > 0) {
      const userProfileInterestCollectionIdentifiers = userProfileInterestCollection.map(
        userProfileInterestItem => getUserProfileInterestIdentifier(userProfileInterestItem)!
      );
      const userProfileInterestsToAdd = userProfileInterests.filter(userProfileInterestItem => {
        const userProfileInterestIdentifier = getUserProfileInterestIdentifier(userProfileInterestItem);
        if (userProfileInterestIdentifier == null || userProfileInterestCollectionIdentifiers.includes(userProfileInterestIdentifier)) {
          return false;
        }
        userProfileInterestCollectionIdentifiers.push(userProfileInterestIdentifier);
        return true;
      });
      return [...userProfileInterestsToAdd, ...userProfileInterestCollection];
    }
    return userProfileInterestCollection;
  }
}
