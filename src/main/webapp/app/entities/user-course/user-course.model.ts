import * as dayjs from 'dayjs';
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
  userId?: string | null;
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
    public userId?: string | null
  ) {}
}

export function getUserCourseIdentifier(userCourse: IUserCourse): number | undefined {
  return userCourse.id;
}
