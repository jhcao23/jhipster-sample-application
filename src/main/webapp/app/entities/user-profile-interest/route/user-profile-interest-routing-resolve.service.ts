import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUserProfileInterest, UserProfileInterest } from '../user-profile-interest.model';
import { UserProfileInterestService } from '../service/user-profile-interest.service';

@Injectable({ providedIn: 'root' })
export class UserProfileInterestRoutingResolveService implements Resolve<IUserProfileInterest> {
  constructor(protected service: UserProfileInterestService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUserProfileInterest> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((userProfileInterest: HttpResponse<UserProfileInterest>) => {
          if (userProfileInterest.body) {
            return of(userProfileInterest.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new UserProfileInterest());
  }
}
