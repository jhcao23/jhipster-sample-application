package technology.touchmars.edu.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import technology.touchmars.edu.domain.enumeration.CourseType;

/**
 * A UserCourse.
 */
@Table("user_course")
public class UserCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("code")
    private String code;

    @Column("course_type")
    private CourseType courseType;

    @Column("name")
    private String name;

    @Column("jhi_desc")
    private String desc;

    @Column("url")
    private String url;

    @Column("cover")
    private byte[] cover;

    @Column("cover_content_type")
    private String coverContentType;

    @Column("begin_dt")
    private ZonedDateTime beginDt;

    @Column("due_dt")
    private ZonedDateTime dueDt;

    @Transient
    private User user;

    @Transient
    private Course course;

    @Column("user_id")
    private Long userId;

    @Column("course_id")
    private Long courseId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserCourse id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public UserCourse code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CourseType getCourseType() {
        return this.courseType;
    }

    public UserCourse courseType(CourseType courseType) {
        this.setCourseType(courseType);
        return this;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public String getName() {
        return this.name;
    }

    public UserCourse name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return this.desc;
    }

    public UserCourse desc(String desc) {
        this.setDesc(desc);
        return this;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return this.url;
    }

    public UserCourse url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[] getCover() {
        return this.cover;
    }

    public UserCourse cover(byte[] cover) {
        this.setCover(cover);
        return this;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    public String getCoverContentType() {
        return this.coverContentType;
    }

    public UserCourse coverContentType(String coverContentType) {
        this.coverContentType = coverContentType;
        return this;
    }

    public void setCoverContentType(String coverContentType) {
        this.coverContentType = coverContentType;
    }

    public ZonedDateTime getBeginDt() {
        return this.beginDt;
    }

    public UserCourse beginDt(ZonedDateTime beginDt) {
        this.setBeginDt(beginDt);
        return this;
    }

    public void setBeginDt(ZonedDateTime beginDt) {
        this.beginDt = beginDt;
    }

    public ZonedDateTime getDueDt() {
        return this.dueDt;
    }

    public UserCourse dueDt(ZonedDateTime dueDt) {
        this.setDueDt(dueDt);
        return this;
    }

    public void setDueDt(ZonedDateTime dueDt) {
        this.dueDt = dueDt;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public UserCourse user(User user) {
        this.setUser(user);
        return this;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
        this.courseId = course != null ? course.getId() : null;
    }

    public UserCourse course(Course course) {
        this.setCourse(course);
        return this;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    public Long getCourseId() {
        return this.courseId;
    }

    public void setCourseId(Long course) {
        this.courseId = course;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserCourse)) {
            return false;
        }
        return id != null && id.equals(((UserCourse) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserCourse{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", courseType='" + getCourseType() + "'" +
            ", name='" + getName() + "'" +
            ", desc='" + getDesc() + "'" +
            ", url='" + getUrl() + "'" +
            ", cover='" + getCover() + "'" +
            ", coverContentType='" + getCoverContentType() + "'" +
            ", beginDt='" + getBeginDt() + "'" +
            ", dueDt='" + getDueDt() + "'" +
            "}";
    }
}
