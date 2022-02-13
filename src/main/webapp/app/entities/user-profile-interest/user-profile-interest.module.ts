import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UserProfileInterestComponent } from './list/user-profile-interest.component';
import { UserProfileInterestDetailComponent } from './detail/user-profile-interest-detail.component';
import { UserProfileInterestUpdateComponent } from './update/user-profile-interest-update.component';
import { UserProfileInterestDeleteDialogComponent } from './delete/user-profile-interest-delete-dialog.component';
import { UserProfileInterestRoutingModule } from './route/user-profile-interest-routing.module';

@NgModule({
  imports: [SharedModule, UserProfileInterestRoutingModule],
  declarations: [
    UserProfileInterestComponent,
    UserProfileInterestDetailComponent,
    UserProfileInterestUpdateComponent,
    UserProfileInterestDeleteDialogComponent,
  ],
  entryComponents: [UserProfileInterestDeleteDialogComponent],
})
export class UserProfileInterestModule {}
