import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { ICourse } from 'app/entities/course/course.model';
import { CourseType } from 'app/entities/enumerations/course-type.model';

export interface IUserCourse {
  id?: number;
  code?: string | null;
  courseType?: CourseType | null;
  name?: string | null;
  desc?: string | null;
  url?: string | null;
  coverContentType?: string | null;
  cover?: string | null;
  beginDt?: dayjs.Dayjs | null;
  dueDt?: dayjs.Dayjs | null;
  user?: IUser;
  course?: ICourse;
}

export class UserCourse implements IUserCourse {
  constructor(
    public id?: number,
    public code?: string | null,
    public courseType?: CourseType | null,
    public name?: string | null,
    public desc?: string | null,
    public url?: string | null,
    public coverContentType?: string | null,
    public cover?: string | null,
    public beginDt?: dayjs.Dayjs | null,
    public dueDt?: dayjs.Dayjs | null,
    public user?: IUser,
    public course?: ICourse
  ) {}
}

export function getUserCourseIdentifier(userCourse: IUserCourse): number | undefined {
  return userCourse.id;
}
