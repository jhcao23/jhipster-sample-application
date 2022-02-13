import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UserProfileInterestDetailComponent } from './user-profile-interest-detail.component';

describe('UserProfileInterest Management Detail Component', () => {
  let comp: UserProfileInterestDetailComponent;
  let fixture: ComponentFixture<UserProfileInterestDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserProfileInterestDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ userProfileInterest: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(UserProfileInterestDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(UserProfileInterestDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load userProfileInterest on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.userProfileInterest).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
