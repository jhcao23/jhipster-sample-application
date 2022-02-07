import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IUserProfileInterest } from '../user-profile-interest.model';
import { UserProfileInterestService } from '../service/user-profile-interest.service';

@Component({
  templateUrl: './user-profile-interest-delete-dialog.component.html',
})
export class UserProfileInterestDeleteDialogComponent {
  userProfileInterest?: IUserProfileInterest;

  constructor(protected userProfileInterestService: UserProfileInterestService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.userProfileInterestService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
