import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IUserProfileInterest, UserProfileInterest } from '../user-profile-interest.model';
import { UserProfileInterestService } from '../service/user-profile-interest.service';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { IInterest } from 'app/entities/interest/interest.model';
import { InterestService } from 'app/entities/interest/service/interest.service';

@Component({
  selector: 'jhi-user-profile-interest-update',
  templateUrl: './user-profile-interest-update.component.html',
})
export class UserProfileInterestUpdateComponent implements OnInit {
  isSaving = false;

  userProfilesSharedCollection: IUserProfile[] = [];
  interestsSharedCollection: IInterest[] = [];

  editForm = this.fb.group({
    id: [],
    code: [],
    userProfile: [null, Validators.required],
    interest: [null, Validators.required],
  });

  constructor(
    protected userProfileInterestService: UserProfileInterestService,
    protected userProfileService: UserProfileService,
    protected interestService: InterestService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userProfileInterest }) => {
      this.updateForm(userProfileInterest);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userProfileInterest = this.createFromForm();
    if (userProfileInterest.id !== undefined) {
      this.subscribeToSaveResponse(this.userProfileInterestService.update(userProfileInterest));
    } else {
      this.subscribeToSaveResponse(this.userProfileInterestService.create(userProfileInterest));
    }
  }

  trackUserProfileById(index: number, item: IUserProfile): number {
    return item.id!;
  }

  trackInterestById(index: number, item: IInterest): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserProfileInterest>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(userProfileInterest: IUserProfileInterest): void {
    this.editForm.patchValue({
      id: userProfileInterest.id,
      code: userProfileInterest.code,
      userProfile: userProfileInterest.userProfile,
      interest: userProfileInterest.interest,
    });

    this.userProfilesSharedCollection = this.userProfileService.addUserProfileToCollectionIfMissing(
      this.userProfilesSharedCollection,
      userProfileInterest.userProfile
    );
    this.interestsSharedCollection = this.interestService.addInterestToCollectionIfMissing(
      this.interestsSharedCollection,
      userProfileInterest.interest
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userProfileService
      .query()
      .pipe(map((res: HttpResponse<IUserProfile[]>) => res.body ?? []))
      .pipe(
        map((userProfiles: IUserProfile[]) =>
          this.userProfileService.addUserProfileToCollectionIfMissing(userProfiles, this.editForm.get('userProfile')!.value)
        )
      )
      .subscribe((userProfiles: IUserProfile[]) => (this.userProfilesSharedCollection = userProfiles));

    this.interestService
      .query()
      .pipe(map((res: HttpResponse<IInterest[]>) => res.body ?? []))
      .pipe(
        map((interests: IInterest[]) =>
          this.interestService.addInterestToCollectionIfMissing(interests, this.editForm.get('interest')!.value)
        )
      )
      .subscribe((interests: IInterest[]) => (this.interestsSharedCollection = interests));
  }

  protected createFromForm(): IUserProfileInterest {
    return {
      ...new UserProfileInterest(),
      id: this.editForm.get(['id'])!.value,
      code: this.editForm.get(['code'])!.value,
      userProfile: this.editForm.get(['userProfile'])!.value,
      interest: this.editForm.get(['interest'])!.value,
    };
  }
}
