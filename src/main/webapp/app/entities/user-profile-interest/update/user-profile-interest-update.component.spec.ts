import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UserProfileInterestService } from '../service/user-profile-interest.service';
import { IUserProfileInterest, UserProfileInterest } from '../user-profile-interest.model';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { IInterest } from 'app/entities/interest/interest.model';
import { InterestService } from 'app/entities/interest/service/interest.service';

import { UserProfileInterestUpdateComponent } from './user-profile-interest-update.component';

describe('UserProfileInterest Management Update Component', () => {
  let comp: UserProfileInterestUpdateComponent;
  let fixture: ComponentFixture<UserProfileInterestUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userProfileInterestService: UserProfileInterestService;
  let userProfileService: UserProfileService;
  let interestService: InterestService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UserProfileInterestUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(UserProfileInterestUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserProfileInterestUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userProfileInterestService = TestBed.inject(UserProfileInterestService);
    userProfileService = TestBed.inject(UserProfileService);
    interestService = TestBed.inject(InterestService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call UserProfile query and add missing value', () => {
      const userProfileInterest: IUserProfileInterest = { id: 456 };
      const userProfile: IUserProfile = { id: 44335 };
      userProfileInterest.userProfile = userProfile;

      const userProfileCollection: IUserProfile[] = [{ id: 42082 }];
      jest.spyOn(userProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: userProfileCollection })));
      const additionalUserProfiles = [userProfile];
      const expectedCollection: IUserProfile[] = [...additionalUserProfiles, ...userProfileCollection];
      jest.spyOn(userProfileService, 'addUserProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userProfileInterest });
      comp.ngOnInit();

      expect(userProfileService.query).toHaveBeenCalled();
      expect(userProfileService.addUserProfileToCollectionIfMissing).toHaveBeenCalledWith(userProfileCollection, ...additionalUserProfiles);
      expect(comp.userProfilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Interest query and add missing value', () => {
      const userProfileInterest: IUserProfileInterest = { id: 456 };
      const interest: IInterest = { id: 7493 };
      userProfileInterest.interest = interest;

      const interestCollection: IInterest[] = [{ id: 6983 }];
      jest.spyOn(interestService, 'query').mockReturnValue(of(new HttpResponse({ body: interestCollection })));
      const additionalInterests = [interest];
      const expectedCollection: IInterest[] = [...additionalInterests, ...interestCollection];
      jest.spyOn(interestService, 'addInterestToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userProfileInterest });
      comp.ngOnInit();

      expect(interestService.query).toHaveBeenCalled();
      expect(interestService.addInterestToCollectionIfMissing).toHaveBeenCalledWith(interestCollection, ...additionalInterests);
      expect(comp.interestsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const userProfileInterest: IUserProfileInterest = { id: 456 };
      const userProfile: IUserProfile = { id: 85906 };
      userProfileInterest.userProfile = userProfile;
      const interest: IInterest = { id: 83821 };
      userProfileInterest.interest = interest;

      activatedRoute.data = of({ userProfileInterest });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(userProfileInterest));
      expect(comp.userProfilesSharedCollection).toContain(userProfile);
      expect(comp.interestsSharedCollection).toContain(interest);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<UserProfileInterest>>();
      const userProfileInterest = { id: 123 };
      jest.spyOn(userProfileInterestService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userProfileInterest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userProfileInterest }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(userProfileInterestService.update).toHaveBeenCalledWith(userProfileInterest);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<UserProfileInterest>>();
      const userProfileInterest = new UserProfileInterest();
      jest.spyOn(userProfileInterestService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userProfileInterest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userProfileInterest }));
      saveSubject.complete();

      // THEN
      expect(userProfileInterestService.create).toHaveBeenCalledWith(userProfileInterest);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<UserProfileInterest>>();
      const userProfileInterest = { id: 123 };
      jest.spyOn(userProfileInterestService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userProfileInterest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userProfileInterestService.update).toHaveBeenCalledWith(userProfileInterest);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackUserProfileById', () => {
      it('Should return tracked UserProfile primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackUserProfileById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackInterestById', () => {
      it('Should return tracked Interest primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackInterestById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
