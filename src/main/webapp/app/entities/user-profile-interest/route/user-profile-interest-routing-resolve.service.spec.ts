import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IUserProfileInterest, UserProfileInterest } from '../user-profile-interest.model';
import { UserProfileInterestService } from '../service/user-profile-interest.service';

import { UserProfileInterestRoutingResolveService } from './user-profile-interest-routing-resolve.service';

describe('UserProfileInterest routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: UserProfileInterestRoutingResolveService;
  let service: UserProfileInterestService;
  let resultUserProfileInterest: IUserProfileInterest | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(UserProfileInterestRoutingResolveService);
    service = TestBed.inject(UserProfileInterestService);
    resultUserProfileInterest = undefined;
  });

  describe('resolve', () => {
    it('should return IUserProfileInterest returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultUserProfileInterest = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultUserProfileInterest).toEqual({ id: 123 });
    });

    it('should return new IUserProfileInterest if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultUserProfileInterest = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultUserProfileInterest).toEqual(new UserProfileInterest());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as UserProfileInterest })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultUserProfileInterest = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultUserProfileInterest).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
