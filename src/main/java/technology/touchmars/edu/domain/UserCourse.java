package technology.touchmars.edu.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import technology.touchmars.edu.domain.enumeration.CourseType;

/**
 * A UserCourse.
 */
@Entity
@Table(name = "user_course")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "course_type")
    private CourseType courseType;

    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "jhi_desc")
    private String desc;

    @Column(name = "url")
    private String url;

    @Lob
    @Column(name = "cover")
    private byte[] cover;

    @Column(name = "cover_content_type")
    private String coverContentType;

    @Column(name = "begin_dt")
    private ZonedDateTime beginDt;

    @Column(name = "due_dt")
    private ZonedDateTime dueDt;

    @Column(name = "user_id")
    private String userId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserCourse id(Long id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return this.code;
    }

    public UserCourse code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CourseType getCourseType() {
        return this.courseType;
    }

    public UserCourse courseType(CourseType courseType) {
        this.courseType = courseType;
        return this;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public String getName() {
        return this.name;
    }

    public UserCourse name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return this.desc;
    }

    public UserCourse desc(String desc) {
        this.desc = desc;
        return this;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return this.url;
    }

    public UserCourse url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[] getCover() {
        return this.cover;
    }

    public UserCourse cover(byte[] cover) {
        this.cover = cover;
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
        this.beginDt = beginDt;
        return this;
    }

    public void setBeginDt(ZonedDateTime beginDt) {
        this.beginDt = beginDt;
    }

    public ZonedDateTime getDueDt() {
        return this.dueDt;
    }

    public UserCourse dueDt(ZonedDateTime dueDt) {
        this.dueDt = dueDt;
        return this;
    }

    public void setDueDt(ZonedDateTime dueDt) {
        this.dueDt = dueDt;
    }

    public String getUserId() {
        return this.userId;
    }

    public UserCourse userId(String userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
            ", userId='" + getUserId() + "'" +
            "}";
    }
}
