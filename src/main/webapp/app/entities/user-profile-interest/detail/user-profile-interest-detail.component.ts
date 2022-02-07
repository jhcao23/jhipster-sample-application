import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUserProfileInterest } from '../user-profile-interest.model';

@Component({
  selector: 'jhi-user-profile-interest-detail',
  templateUrl: './user-profile-interest-detail.component.html',
})
export class UserProfileInterestDetailComponent implements OnInit {
  userProfileInterest: IUserProfileInterest | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userProfileInterest }) => {
      this.userProfileInterest = userProfileInterest;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
