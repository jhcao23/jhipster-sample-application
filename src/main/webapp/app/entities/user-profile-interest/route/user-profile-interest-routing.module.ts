import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UserProfileInterestComponent } from '../list/user-profile-interest.component';
import { UserProfileInterestDetailComponent } from '../detail/user-profile-interest-detail.component';
import { UserProfileInterestUpdateComponent } from '../update/user-profile-interest-update.component';
import { UserProfileInterestRoutingResolveService } from './user-profile-interest-routing-resolve.service';

const userProfileInterestRoute: Routes = [
  {
    path: '',
    component: UserProfileInterestComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UserProfileInterestDetailComponent,
    resolve: {
      userProfileInterest: UserProfileInterestRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UserProfileInterestUpdateComponent,
    resolve: {
      userProfileInterest: UserProfileInterestRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UserProfileInterestUpdateComponent,
    resolve: {
      userProfileInterest: UserProfileInterestRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(userProfileInterestRoute)],
  exports: [RouterModule],
})
export class UserProfileInterestRoutingModule {}
