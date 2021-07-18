import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUserCourse, getUserCourseIdentifier } from '../user-course.model';

export type EntityResponseType = HttpResponse<IUserCourse>;
export type EntityArrayResponseType = HttpResponse<IUserCourse[]>;

@Injectable({ providedIn: 'root' })
export class UserCourseService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/user-courses');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(userCourse: IUserCourse): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userCourse);
    return this.http
      .post<IUserCourse>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(userCourse: IUserCourse): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userCourse);
    return this.http
      .put<IUserCourse>(`${this.resourceUrl}/${getUserCourseIdentifier(userCourse) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(userCourse: IUserCourse): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userCourse);
    return this.http
      .patch<IUserCourse>(`${this.resourceUrl}/${getUserCourseIdentifier(userCourse) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IUserCourse>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IUserCourse[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addUserCourseToCollectionIfMissing(
    userCourseCollection: IUserCourse[],
    ...userCoursesToCheck: (IUserCourse | null | undefined)[]
  ): IUserCourse[] {
    const userCourses: IUserCourse[] = userCoursesToCheck.filter(isPresent);
    if (userCourses.length > 0) {
      const userCourseCollectionIdentifiers = userCourseCollection.map(userCourseItem => getUserCourseIdentifier(userCourseItem)!);
      const userCoursesToAdd = userCourses.filter(userCourseItem => {
        const userCourseIdentifier = getUserCourseIdentifier(userCourseItem);
        if (userCourseIdentifier == null || userCourseCollectionIdentifiers.includes(userCourseIdentifier)) {
          return false;
        }
        userCourseCollectionIdentifiers.push(userCourseIdentifier);
        return true;
      });
      return [...userCoursesToAdd, ...userCourseCollection];
    }
    return userCourseCollection;
  }

  protected convertDateFromClient(userCourse: IUserCourse): IUserCourse {
    return Object.assign({}, userCourse, {
      beginDt: userCourse.beginDt?.isValid() ? userCourse.beginDt.toJSON() : undefined,
      dueDt: userCourse.dueDt?.isValid() ? userCourse.dueDt.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.beginDt = res.body.beginDt ? dayjs(res.body.beginDt) : undefined;
      res.body.dueDt = res.body.dueDt ? dayjs(res.body.dueDt) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((userCourse: IUserCourse) => {
        userCourse.beginDt = userCourse.beginDt ? dayjs(userCourse.beginDt) : undefined;
        userCourse.dueDt = userCourse.dueDt ? dayjs(userCourse.dueDt) : undefined;
      });
    }
    return res;
  }
}
