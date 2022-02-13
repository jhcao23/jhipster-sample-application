import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IUserProfileInterest } from '../user-profile-interest.model';

import { ASC, DESC, ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { UserProfileInterestService } from '../service/user-profile-interest.service';
import { UserProfileInterestDeleteDialogComponent } from '../delete/user-profile-interest-delete-dialog.component';
import { ParseLinks } from 'app/core/util/parse-links.service';

@Component({
  selector: 'jhi-user-profile-interest',
  templateUrl: './user-profile-interest.component.html',
})
export class UserProfileInterestComponent implements OnInit {
  userProfileInterests: IUserProfileInterest[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(
    protected userProfileInterestService: UserProfileInterestService,
    protected modalService: NgbModal,
    protected parseLinks: ParseLinks
  ) {
    this.userProfileInterests = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  loadAll(): void {
    this.isLoading = true;

    this.userProfileInterestService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<IUserProfileInterest[]>) => {
          this.isLoading = false;
          this.paginateUserProfileInterests(res.body, res.headers);
        },
        error: () => {
          this.isLoading = false;
        },
      });
  }

  reset(): void {
    this.page = 0;
    this.userProfileInterests = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IUserProfileInterest): number {
    return item.id!;
  }

  delete(userProfileInterest: IUserProfileInterest): void {
    const modalRef = this.modalService.open(UserProfileInterestDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.userProfileInterest = userProfileInterest;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.reset();
      }
    });
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? ASC : DESC)];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateUserProfileInterests(data: IUserProfileInterest[] | null, headers: HttpHeaders): void {
    const linkHeader = headers.get('link');
    if (linkHeader) {
      this.links = this.parseLinks.parse(linkHeader);
    } else {
      this.links = {
        last: 0,
      };
    }
    if (data) {
      for (const d of data) {
        this.userProfileInterests.push(d);
      }
    }
  }
}
