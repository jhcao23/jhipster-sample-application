package technology.touchmars.edu.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link technology.touchmars.edu.domain.UserProfile} entity. This class is used
 * in {@link technology.touchmars.edu.web.rest.UserProfileResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-profiles?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class UserProfileCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter interests;

    private StringFilter userId;

    private LongFilter userId;

    public UserProfileCriteria() {}

    public UserProfileCriteria(UserProfileCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.interests = other.interests == null ? null : other.interests.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
    }

    @Override
    public UserProfileCriteria copy() {
        return new UserProfileCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getInterests() {
        return interests;
    }

    public StringFilter interests() {
        if (interests == null) {
            interests = new StringFilter();
        }
        return interests;
    }

    public void setInterests(StringFilter interests) {
        this.interests = interests;
    }

    public StringFilter getUserId() {
        return userId;
    }

    public StringFilter userId() {
        if (userId == null) {
            userId = new StringFilter();
        }
        return userId;
    }

    public void setUserId(StringFilter userId) {
        this.userId = userId;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public LongFilter userId() {
        if (userId == null) {
            userId = new LongFilter();
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final UserProfileCriteria that = (UserProfileCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(interests, that.interests) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(userId, that.userId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, interests, userId, userId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserProfileCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (interests != null ? "interests=" + interests + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            "}";
    }
}
